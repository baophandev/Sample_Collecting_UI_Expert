package com.application.data.paging

import com.application.data.entity.Post
import com.application.data.repository.PostRepository
import com.sc.library.user.repository.UserRepository

class PostPagingSource(
    private val isAnswered: Boolean,
    private val title: String,
    private val userRepository: UserRepository,
    private val postRepository: PostRepository,
) : AbstractPagingSource<Post>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Post> {
        val nextPageNumber = params.key ?: 0
        val expertId = userRepository.loggedUser?.id
            ?: return LoadResult.Error(Exception("Cannot get logged user data."))

        val response = postRepository.getPostsByExpert(
            expertId = expertId,
            title = title,
            isAnswered = isAnswered,
            pageNumber = nextPageNumber,
            pageSize = params.loadSize
        )
        return retrievePagingForward(nextPageNumber = nextPageNumber, response = response)
    }

}