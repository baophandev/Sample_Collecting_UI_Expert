package com.application.data.repository

import android.net.Uri
import android.util.Log
import com.application.R
import com.application.data.datasource.IPostService
import com.application.data.entity.Comment
import com.application.data.entity.FileInPost
import com.application.data.entity.GeneralComment
import com.application.data.entity.Post
import com.application.data.entity.request.CreateCommentRequest
import com.application.data.entity.response.CommentResponse
import com.application.data.entity.response.FileInPostResponse
import com.application.data.entity.response.PostResponse
import com.application.data.exception.PostException
import io.github.nhatbangle.sc.attachment.repository.AttachmentRepository
import io.github.nhatbangle.sc.user.repository.UserRepository
import io.github.nhatbangle.sc.utility.client.response.PagingResponse
import io.github.nhatbangle.sc.utility.state.ResourceState
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.runBlocking

class PostRepository(
    private val userRepository: UserRepository,
    private val atmRepository: AttachmentRepository,
    private val service: IPostService
) {
    private val cachedPosts: MutableMap<String, Post> = mutableMapOf()

    /**
     * Retrieves a post by its ID, optionally skipping the cached version.
     *
     * This function first checks if the post is already cached and returns it if found and `skipCached` is false.
     * Otherwise, it makes a network request to fetch the post, caches it, and emits it as a `ResourceState.Success`.
     * If any error occurs during the process, it emits a `ResourceState.Error` with an appropriate message.
     *
     * @param postId The ID of the post to retrieve.
     * @param skipCached Whether to skip the cached version and force a network request (defaults to false).
     * @return A flow emitting [ResourceState] objects representing the post retrieval progress and result.
     *         - [ResourceState.Success] indicates successful retrieval with the post data.
     *         - [ResourceState.Error] indicates an error during the retrieval process.
     */
    fun getPost(postId: String, skipCached: Boolean = false): Flow<ResourceState<Post>> {
        if (!skipCached && cachedPosts.containsKey(postId))
            return flowOf(ResourceState.Success(cachedPosts[postId]!!))

        return flow<ResourceState<Post>> {
            val response = service.getPost(postId)
            val post = mapResponseToPost(response)
            cachedPosts[postId] = post
            emit(ResourceState.Success(post))
        }.catch {
            Log.e(TAG, it.message, it)
            emit(
                ResourceState.Error(
                    message = "Cannot get post by id",
                    resId = R.string.get_post_error
                )
            )
        }
    }

    suspend fun getFilesInPost(
        postId: String
    ): Flow<ResourceState<List<FileInPost>>> = flow<ResourceState<List<FileInPost>>> {
        val totalElements = service
            .getFilesInPost(postId = postId, pageNumber = 0, pageSize = 1)
            .totalElements
        val files = service
            .getFilesInPost(postId = postId, pageNumber = 0, pageSize = totalElements.toInt())
            .content.map { mapResponseToFilesInPost(postId, it) }
        emit(ResourceState.Success(files))
    }.catch { exception ->
        Log.e(TAG, exception.message, exception)
        emit(
            ResourceState.Error(
                message = "Cannot get all files in post.",
                resId = R.string.get_files_in_post_error,
            )
        )
    }

    suspend fun getPostsByExpert(
        expertId: String,
        title: String,
        isAnswered: Boolean = false,
        pageNumber: Int = 0,
        pageSize: Int = 6,
    ): Result<PagingResponse<Post>> = runCatching {
        service.getPostsByExpert(
            expertId = expertId,
            title = title,
            isAnswered = isAnswered,
            pageNumber = pageNumber,
            pageSize = pageSize
        ).map(::mapResponseToPost)
    }.onFailure { Log.e(TAG, it.message, it) }

    suspend fun createGeneralComment(
        postId: String,
        content: String,
        attachments: List<Uri>? = null
    ): Flow<ResourceState<Boolean>> = flow<ResourceState<Boolean>> {
        val attachmentIds = runBlocking {
            attachments?.map {
                async {
                    when (val rsState = atmRepository.storeAttachment(it).last()) {
                        is ResourceState.Success -> rsState.data
                        is ResourceState.Error -> throw PostException
                            .AttachmentStoringException("Cannot store attachment.")

                    }
                }
            }?.awaitAll() ?: emptyList()
        }
        val body = CreateCommentRequest(
            content = content,
            attachmentIds = attachmentIds
        )

        val result = service.createGeneralComment(
            postId = postId,
            body = body
        )
        emit(ResourceState.Success(result))
    }.catch {
        Log.e(TAG, it.message, it)
        emit(
            ResourceState.Error(
                message = "Cannot create general comment.",
                resId = R.string.create_general_comment_error
            )
        )
    }

    suspend fun createComment(
        fileId: String,
        content: String,
        attachments: List<Uri>? = null
    ): Flow<ResourceState<Boolean>> = flow<ResourceState<Boolean>> {
        val attachmentIds = runBlocking {
            attachments?.map {
                async {
                    when (val rsState = atmRepository.storeAttachment(it).last()) {
                        is ResourceState.Success -> rsState.data
                        is ResourceState.Error -> throw PostException
                            .AttachmentStoringException("Cannot store attachment.")

                    }
                }
            }?.awaitAll() ?: emptyList()
        }
        val body = CreateCommentRequest(
            content = content,
            attachmentIds = attachmentIds
        )

        val result = service.createComment(
            fileId = fileId,
            body = body
        )
        emit(ResourceState.Success(result))
    }.catch {
        Log.e(TAG, it.message, it)
        emit(
            ResourceState.Error(
                message = "Cannot create comment.",
                resId = R.string.create_comment_error
            )
        )
    }

    /**
     * @throws [PostException.UserRetrievingException] if cannot retrieve user data.
     * @throws [PostException.AttachmentRetrievingException] if cannot retrieve attachment data.
     */
    private fun mapResponseToPost(response: PostResponse): Post {
        val (userPair, thumbnail, generalComment) = runBlocking {
            val owner = async {
                when (val resourceState = userRepository.getUser(response.ownerId).last()) {
                    is ResourceState.Error -> throw PostException
                        .UserRetrievingException("Cannot retrieve an owner of post.")

                    is ResourceState.Success -> resourceState.data
                }
            }.await()
            val expert = async {
                if (response.expertId.isNotBlank())
                    when (val rsState = userRepository.getUser(response.expertId).last()) {
                        is ResourceState.Error -> throw PostException
                            .UserRetrievingException("Cannot retrieve an expert of post.")

                        is ResourceState.Success -> rsState.data
                    } else null
            }.await()
            val thumbnail = async {
                response.fileIds.getOrNull(0)?.let { id ->
                    when (val rsState = atmRepository.getAttachment(id).last()) {
                        is ResourceState.Error -> null
                        is ResourceState.Success -> Uri.parse(rsState.data.url)
                    }
                }
            }.await()
            val generalComment = response.generalComment?.let { comment ->
                GeneralComment(
                    content = comment.content,
                    attachments = comment.attachmentIds.map { attachmentId ->
                        async {
                            when (val rsState = atmRepository.getAttachment(attachmentId).last()) {
                                is ResourceState.Success -> rsState.data
                                is ResourceState.Error -> throw PostException
                                    .AttachmentRetrievingException("Cannot retrieve an attachment of post.")
                            }
                        }
                    }.awaitAll()
                )
            }

            Triple(Pair(owner, expert), thumbnail, generalComment)
        }

        return Post(
            id = response.postId,
            isResolved = response.status,
            thumbnail = thumbnail,
            createdAt = response.createdAt,
            title = response.title,
            owner = userPair.first,
            expert = userPair.second,
            generalComment = generalComment
        )
    }

    /**
     * @throws [PostException.AttachmentRetrievingException]
     */
    private fun mapResponseToFilesInPost(postId: String, response: FileInPostResponse): FileInPost {
        val (image, comment) = runBlocking {
            val image = async {
                when (val rsState = atmRepository.getAttachment(response.fileId).last()) {
                    is ResourceState.Success -> Uri.parse(rsState.data.url)
                    is ResourceState.Error -> throw PostException
                        .AttachmentRetrievingException("Cannot retrieve an attachment of post.")
                }
            }
            val comment = async { mapResponseToComment(response.comment) }

            Pair(image.await(), comment.await())
        }

        return FileInPost(
            id = response.fileId,
            image = image,
            description = response.description,
            comment = comment,
            postId = postId
        )
    }

    private fun mapResponseToComment(response: CommentResponse): Comment {
        val attachments = runBlocking {
            response.attachmentIds.map { id ->
                async {
                    when (val rsState = atmRepository.getAttachment(id).last()) {
                        is ResourceState.Success -> rsState.data
                        is ResourceState.Error -> throw PostException
                            .AttachmentRetrievingException("Cannot retrieve an attachment of post.")
                    }
                }
            }.awaitAll()
        }

        return Comment(
            content = response.content,
            attachments = attachments
        )
    }

    companion object {
        const val TAG = "PostRepository"
    }

}