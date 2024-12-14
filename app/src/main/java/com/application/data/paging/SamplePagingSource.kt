package com.application.data.paging

import com.application.data.entity.Sample
import com.application.data.repository.SampleRepository

class SamplePagingSource(
    private val stageId: String,
    private val repository: SampleRepository
) : AbstractPagingSource<Sample>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Sample> {
        val nextPageNumber = params.key ?: 0
        val result = repository.getAllSamplesOfStage(
            stageId = stageId,
            pageNumber = nextPageNumber,
            pageSize = params.loadSize
        )

        return retrievePagingForward(nextPageNumber, result)
    }

    companion object {
        const val TAG = "SamplePagingSource"
    }

}