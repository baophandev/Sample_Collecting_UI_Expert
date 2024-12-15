package com.application.data.paging

import com.application.data.entity.Stage
import com.application.data.repository.StageRepository

class StagePagingSource(
    private val projectId: String,
    private val stageRepository: StageRepository
) : AbstractPagingSource<Stage>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Stage> {
        val nextPageNumber = params.key ?: 0
        val result = stageRepository.getAllStages(
            projectId = projectId,
            pageNumber = nextPageNumber,
            pageSize = params.loadSize
        )
        return retrievePagingForward(nextPageNumber, result)
    }

    companion object {
        const val TAG = "StagePagingSource"
    }

}