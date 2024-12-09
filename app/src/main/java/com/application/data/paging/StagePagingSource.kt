package com.application.data.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.application.data.datasource.IProjectService
import com.application.data.entity.Stage
import com.application.data.entity.response.StageResponse
import io.ktor.client.plugins.ClientRequestException
import java.io.IOException

class StagePagingSource(
    private val projectId: String,
    private val projectService: IProjectService,
) : PagingSource<Int, Stage>() {

    override fun getRefreshKey(state: PagingState<Int, Stage>): Int? {
        // We need to get the previous key (or next key if previous is null) of the page
        // that was closest to the most recently accessed index.
        // Anchor position is the most recently accessed index
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Stage> {
        val nextPageNumber = params.key ?: 0
        try {
            val response = projectService.getAllStages(
                projectId = projectId,
                pageNumber = nextPageNumber,
                pageSize = params.loadSize
            )

            val stages = response.content.map { mapResponseToProject(it) }
            val nextKey = if (!response.last) nextPageNumber + 1 else null
            return LoadResult.Page(
                data = stages,
                prevKey = null, // Only paging forward.
                nextKey = nextKey
            )
        } catch (e: IOException) {
            Log.e(TAG, e.message, e)
            return LoadResult.Error(e)
        } catch (e: ClientRequestException) {
            Log.e(TAG, e.message, e)
            return LoadResult.Error(e)
        }
    }

    private fun mapResponseToProject(response: StageResponse): Stage {
        return Stage(
            id = response.id,
            name = response.name,
            description = response.description,
            projectOwnerId = response.projectOwnerId,
            startDate = response.startDate,
            endDate = response.endDate,
            formId = response.formId
        )
    }

    companion object {
        const val TAG = "StagePagingSource"
    }

}