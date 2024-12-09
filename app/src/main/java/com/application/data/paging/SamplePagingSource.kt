package com.application.data.paging

import com.application.data.entity.Sample
import com.application.data.repository.SampleRepository

class SamplePagingSource(
    private val stageId: String,
    private val repository: SampleRepository
) : AbstractPagingSource<Sample>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Sample> {
        val nextPageNumber = params.key ?: 0
        return repository.getAllSamplesOfStage(
            stageId = stageId,
            pageNumber = nextPageNumber,
            pageSize = params.loadSize
        ).map {
            val nextKey = if (!it.last) nextPageNumber + 1 else null
            LoadResult.Page(
                data = it.content,
                prevKey = null, // Only paging forward.
                nextKey = nextKey
            )
        }.getOrElse { LoadResult.Error(it) }
    }

    companion object {
        const val TAG = "SamplePagingSource"
    }

}