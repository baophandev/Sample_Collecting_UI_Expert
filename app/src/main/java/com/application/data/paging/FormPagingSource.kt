package com.application.data.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.application.data.datasource.IProjectService
import com.application.data.entity.Form
import com.application.data.entity.response.FormResponse
import io.ktor.client.plugins.ClientRequestException
import java.io.IOException

class FormPagingSource(
    private val projectId: String,
    private val projectService: IProjectService,
) : PagingSource<Int, Form>() {

    override fun getRefreshKey(state: PagingState<Int, Form>): Int? {
        // We need to get the previous key (or next key if previous is null) of the page
        // that was closest to the most recently accessed index.
        // Anchor position is the most recently accessed index
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Form> {
        val nextPageNumber = params.key ?: 0
        try {
            val response = projectService.getAllForms(
                projectId = projectId,
                pageNumber = nextPageNumber,
                pageSize = params.loadSize
            )

            val forms = response.content.map(this::mapResponseToForm)
            val nextKey = if (!response.last) nextPageNumber + 1 else null
            return LoadResult.Page(
                data = forms,
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

    private fun mapResponseToForm(response: FormResponse): Form {
        return Form(
            id = response.id,
            description = response.description,
            title = response.title,
            projectOwnerId = response.projectOwnerId
        )
    }

    companion object {
        const val TAG = "FormPagingSource"
    }

}