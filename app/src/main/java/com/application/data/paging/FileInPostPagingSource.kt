package com.application.data.paging

import com.application.data.entity.FileInPost
import com.application.data.repository.PostRepository

class FileInPostPagingSource(
    private val postId: String,
    private val postRepository: PostRepository,
) : AbstractPagingSource<FileInPost>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FileInPost> {
        val nextPageNumber = params.key ?: 0
        val result = postRepository.getFilesInPost(
            postId = postId,
            pageNumber = nextPageNumber,
            pageSize = params.loadSize
        )
        return retrievePagingForward(nextPageNumber, result)
    }

}