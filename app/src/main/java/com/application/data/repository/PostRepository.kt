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
import com.sc.library.attachment.repository.AttachmentRepository
import com.sc.library.user.repository.UserRepository
import com.sc.library.utility.client.response.PagingResponse
import com.sc.library.utility.state.ResourceState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last

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
        postId: String,
        pageNumber: Int = 0,
        pageSize: Int = 6
    ): Result<PagingResponse<FileInPost>> = runCatching {
        val response = service.getFilesInPost(postId, pageNumber, pageSize)
        val fileInPosts = response.content.map { mapResponseToFilesInPost(it) }
        PagingResponse(
            totalPages = response.totalPages,
            totalElements = response.totalElements,
            number = response.number,
            size = response.size,
            numberOfElements = response.numberOfElements,
            first = response.first,
            last = response.last,
            content = fileInPosts
        )
    }.onFailure { Log.e(TAG, it.message, it) }

    suspend fun getPostsByExpert(
        expertId: String,
        isAnswered: Boolean = false,
        pageNumber: Int = 0,
        pageSize: Int = 6,
    ): Result<PagingResponse<Post>> = runCatching {
        val response = service.getPostsByExpert(
            expertId = expertId,
            isAnswered = isAnswered,
            pageNumber = pageNumber,
            pageSize = pageSize
        )
        val posts = response.content.map { mapResponseToPost(it) }
        PagingResponse(
            totalPages = response.totalPages,
            totalElements = response.totalElements,
            number = response.number,
            size = response.size,
            numberOfElements = response.numberOfElements,
            first = response.first,
            last = response.last,
            content = posts
        )
    }.onFailure { Log.e(TAG, it.message, it) }

    suspend fun createGeneralComment(
        postId: String,
        content: String,
        attachmentIds: List<String>? = null
    ): Flow<ResourceState<Boolean>> = flow<ResourceState<Boolean>> {
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
        attachmentIds: List<String>? = null
    ): Flow<ResourceState<Boolean>> = flow<ResourceState<Boolean>> {
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
    private suspend fun mapResponseToPost(response: PostResponse): Post {
        val owner = when (val resourceState = userRepository.getUser(response.ownerId).last()) {
            is ResourceState.Error -> throw PostException
                .UserRetrievingException("Cannot retrieve an owner of post.")

            is ResourceState.Success -> resourceState.data
        }
        val expert = if (response.expertId.isNotBlank())
            when (val rsState = userRepository.getUser(response.expertId).last()) {
                is ResourceState.Error -> throw PostException
                    .UserRetrievingException("Cannot retrieve an expert of post.")

                is ResourceState.Success -> rsState.data
            } else null
        val thumbnail = response.fileIds.getOrNull(0)?.let { id ->
            when (val rsState = atmRepository.getAttachment(id).last()) {
                is ResourceState.Error -> null
                is ResourceState.Success -> Uri.parse(rsState.data.url)
            }
        }
        val generalComment = response.generalComment?.let { comment ->
            GeneralComment(
                content = comment.content,
                attachments = comment.attachmentIds.map { attachmentId ->
                    when (val rsState = atmRepository.getAttachment(attachmentId).last()) {
                        is ResourceState.Success -> rsState.data
                        is ResourceState.Error -> throw PostException
                            .AttachmentRetrievingException("Cannot retrieve an attachment of post.")
                    }
                }
            )
        }

        return Post(
            id = response.postId,
            thumbnail = thumbnail,
            createdAt = response.createdAt,
            title = response.title,
            owner = owner,
            expert = expert,
            generalComment = generalComment
        )
    }

    /**
     * @throws [PostException.AttachmentRetrievingException]
     */
    private suspend fun mapResponseToFilesInPost(response: FileInPostResponse): FileInPost {
        val image = when (val rsState = atmRepository.getAttachment(response.fileId).last()) {
            is ResourceState.Success -> Uri.parse(rsState.data.url)
            is ResourceState.Error -> throw PostException
                .AttachmentRetrievingException("Cannot retrieve an attachment of post.")
        }
        val comment = mapResponseToComment(response.comment)

        return FileInPost(
            id = response.fileId,
            image = image,
            description = response.content,
            comment = comment
        )
    }

    private suspend fun mapResponseToComment(response: CommentResponse): Comment {
        val attachments = response.attachmentIds.map { id ->
            when (val rsState = atmRepository.getAttachment(id).last()) {
                is ResourceState.Success -> rsState.data
                is ResourceState.Error -> throw PostException
                    .AttachmentRetrievingException("Cannot retrieve an attachment of post.")
            }
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