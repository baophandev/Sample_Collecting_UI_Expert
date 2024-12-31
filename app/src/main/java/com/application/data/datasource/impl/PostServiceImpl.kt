package com.application.data.datasource.impl

import com.application.data.datasource.IPostService
import com.application.data.entity.request.CreateCommentRequest
import com.application.data.entity.response.FileInPostResponse
import com.application.data.entity.response.PostResponse
import io.github.nhatbangle.sc.utility.client.response.PagingResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType

class PostServiceImpl(
    private val client: HttpClient,
    private val prefixPath: String = "api",
) : IPostService {

    /**
     * If the network request fails, it returns a default `PagingResponse` object with empty data.
     * @see IPostService.getPostsByExpert
     */
    override suspend fun getPostsByExpert(
        expertId: String,
        title: String,
        isAnswered: Boolean,
        pageNumber: Int,
        pageSize: Int
    ): PagingResponse<PostResponse> = runCatching<PagingResponse<PostResponse>> {
        client.get(urlString = "$prefixPath/post/getByExpertId/$expertId") {
            url {
                encodedParameters.append("isAnswered", "$isAnswered")
                encodedParameters.append("title", title)
                encodedParameters.append("pageNumber", "$pageNumber")
                encodedParameters.append("pageSize", "$pageSize")
            }
        }.body()
    }.getOrDefault(PagingResponse())

    /**
     * @throws [RedirectResponseException] For 3xx responses, indicating redirection.
     * @throws [ClientRequestException] For 4xx responses, indicating client errors.
     * @throws [ServerResponseException] For 5xx responses, indicating server errors.
     * @see IPostService.getPost
     */
    override suspend fun getPost(postId: String): PostResponse = client
        .get(urlString = "$prefixPath/post/$postId")
        .body()

    /**
     * @throws [RedirectResponseException] For 3xx responses, indicating redirection.
     * @throws [ClientRequestException] For 4xx responses, indicating client errors.
     * @throws [ServerResponseException] For 5xx responses, indicating server errors.
     * @see IPostService.getFilesInPost
     */
    override suspend fun getFilesInPost(
        postId: String,
        pageNumber: Int,
        pageSize: Int
    ): PagingResponse<FileInPostResponse> = runCatching<PagingResponse<FileInPostResponse>> {
        client.get(urlString = "$prefixPath/post/$postId/getFilesInPost") {
            url {
                encodedParameters.append("pageNumber", "$pageNumber")
                encodedParameters.append("pageSize", "$pageSize")
            }
        }.body()
    }.getOrDefault(PagingResponse())

    /**
     * @throws [RedirectResponseException] For 3xx responses, indicating redirection.
     * @throws [ClientRequestException] For 4xx responses, indicating client errors.
     * @throws [ServerResponseException] For 5xx responses, indicating server errors.
     * @see IPostService.createGeneralComment
     */
    override suspend fun createGeneralComment(
        postId: String,
        body: CreateCommentRequest
    ): Boolean = client
        .post("$prefixPath/comment/addComment/$postId") {
            contentType(ContentType.Application.Json)
            setBody(body)
        }.status == HttpStatusCode.Created

    /**
     * @throws [RedirectResponseException] For 3xx responses, indicating redirection.
     * @throws [ClientRequestException] For 4xx responses, indicating client errors.
     * @throws [ServerResponseException] For 5xx responses, indicating server errors.
     * @see IPostService.createComment
     */
    override suspend fun createComment(
        fileId: String,
        body: CreateCommentRequest
    ): Boolean = client
        .post("$prefixPath/comment/addFileComments/$fileId") {
            contentType(ContentType.Application.Json)
            setBody(body)
        }.status == HttpStatusCode.Created

}

