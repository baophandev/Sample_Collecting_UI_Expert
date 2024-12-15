package com.application.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.sc.library.utility.client.response.PagingResponse

abstract class AbstractPagingSource<T : Any> : PagingSource<Int, T>() {

    override fun getRefreshKey(state: PagingState<Int, T>): Int? {
        // We need to get the previous key (or next key if previous is null) of the page
        // that was closest to the most recently accessed index.
        // Anchor position is the most recently accessed index
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    protected fun <T : Any> retrievePagingForward(
        nextPageNumber: Int,
        response: Result<PagingResponse<T>>
    ): LoadResult<Int, T> = response
        .map {
            val nextKey = if (!it.last) nextPageNumber + 1 else null
            return LoadResult.Page(
                data = it.content,
                prevKey = null, // Only paging forward.
                nextKey = nextKey
            )
        }.getOrElse { LoadResult.Error(it) }

}