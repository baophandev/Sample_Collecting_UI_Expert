package com.application.data.datasource

import com.application.data.entity.request.CreateCommentRequest
import com.application.data.entity.response.CommentResponse
import com.application.data.entity.response.FileInPostResponse
import com.application.data.entity.response.GeneralCommentResponse
import com.application.data.entity.response.PostResponse
import com.sc.library.utility.client.response.PagingResponse

interface IPostService {

    /**
     * Retrieves a paginated list of posts by expert ID, optionally filtering by answered status.
     *
     * This function sends a request to the server to retrieve a list of posts associated with a specific expert,
     * identified by their `expertId`. It allows filtering the posts by their answered status using the `isAnswered` parameter.
     * The results are paginated, and the function returns a `PagingResponse` object containing the retrieved posts and pagination information.
     *
     * @param expertId The ID of the expert for which to retrieve posts.
     * @param isAnswered A boolean flag indicating whether to retrieve only answered posts (`true`) or only unanswered posts (`false`, default).
     * @param pageNumber The page number to retrieve (defaults to 0).
     * @param pageSize The number of posts per page (defaults to 6).
     * @return A [PagingResponse] object containing a list of [PostResponse] objects and pagination information.
     */
    suspend fun getPostsByExpert(
        expertId: String,
        isAnswered: Boolean = false,
        pageNumber: Int = 0,
        pageSize: Int = 6
    ): PagingResponse<PostResponse>

    /**
     * Retrieves a post by its ID.
     *
     * This function sends a request to the server to retrieve the post with the specified ID.
     * It returns a `PostResponse` object containing the post data.
     *
     * @param postId The ID of the post to retrieve.
     * @return A [PostResponse] object containing the post data.
     */
    suspend fun getPost(postId: String): PostResponse

    /**
     * Retrieves a paginated list of files associated with a post.
     *
     * This function sends a request to the server to retrieve files related to a specific post,
     * identified by the provided `postId`. It returns a `PagingResponse` containing a list
     * of `FileInPostResponse` objects and pagination information.
     *
     * @param postId The ID of the post for which to retrieve files.
     * @param pageNumber The page number to retrieve (defaults to 0).
     * @param pageSize The number of files per page (defaults to 6).
     * @return A [PagingResponse] object containing a list of [FileInPostResponse] objects and pagination information.
     */
    suspend fun getFilesInPost(
        postId: String,
        pageNumber: Int = 0,
        pageSize: Int = 6
    ): PagingResponse<FileInPostResponse>

    /**
     * Creates a new general comment on a post.
     *
     * This function sends a request to the server to create a new general comment associated with the specified post.
     * It returns a `GeneralCommentResponse` object containing the details of the newly created comment.
     *
     * @param postId The ID of the post to which the comment will be added.
     * @param body The [CreateCommentRequest] object containing the content and other details of the comment.
     * @return `true` if created successfully, otherwise `false`.
     */
    suspend fun createGeneralComment(
        postId: String,
        body: CreateCommentRequest
    ): Boolean

    /**
     * Creates a new comment on a file.
     *
     * This function sends a request to the server to create a new comment associated with the specified file.
     * It returns a `CommentResponse` object containing the details of the newly created comment.
     *
     * @param fileId The ID of the file to which the comment will be added.
     * @param body The [CreateCommentRequest] object containing the content and other details of the comment.
     * @return `true` if created successfully, otherwise `false`.
     */
    suspend fun createComment(fileId: String, body: CreateCommentRequest): Boolean

}