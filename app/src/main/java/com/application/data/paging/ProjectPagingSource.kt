package com.application.data.paging

import android.net.Uri
import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.application.data.datasource.IProjectService
import com.application.data.entity.Project
import com.application.data.entity.response.ProjectResponse
import com.application.data.repository.AttachmentRepository
import com.application.data.repository.UserRepository
import com.application.util.ResourceState
import io.ktor.client.plugins.ClientRequestException
import kotlinx.coroutines.flow.last
import java.io.IOException

class ProjectPagingSource(
    private val projectService: IProjectService,
    private val userRepository: UserRepository,
    private val attachmentRepository: AttachmentRepository
) : PagingSource<Int, Project>() {

    override fun getRefreshKey(state: PagingState<Int, Project>): Int {
        // Try to find the page key of the closest page to anchorPosition from
        // either the prevKey or the nextKey; you need to handle nullability here.
        //  * prevKey == null -> anchorPage is the first page.
        //  * nextKey == null -> anchorPage is the last page.
        //  * both prevKey and nextKey are null -> anchorPage is the initial page, so return null.
        return ((state.anchorPosition ?: 0) - state.config.initialLoadSize / 2)
            .coerceAtLeast(0)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Project> {
        val nextPageNumber = params.key ?: 0
        val user = userRepository.getLoggedUser()
            ?: return LoadResult.Error(Exception("Cannot get projects because of user not logged in"))
        try {
            val response = projectService.getAllProjects(
                userId = user.id,
                pageNumber = nextPageNumber,
                pageSize = params.loadSize
            )

            val projects = response.content.map { mapResponseToProject(it) }
            return LoadResult.Page(
                data = projects,
                prevKey = null, // Only paging forward.
                nextKey = if (nextPageNumber + 1 < response.totalPages) nextPageNumber + 1 else null
            )
        } catch (e: IOException) {
            Log.e(TAG, e.message, e)
            return LoadResult.Error(e)
        } catch (e: ClientRequestException) {
            Log.e(TAG, e.message, e)
            return LoadResult.Error(e)
        }
    }

    private suspend fun mapResponseToProject(response: ProjectResponse): Project {
        val ownerState = userRepository.getUser(response.ownerId).last()
        val owner = if (ownerState is ResourceState.Success)
            ownerState.data else UserRepository.DEFAULT_USER.copy()
        val atmState = if (response.thumbnailId != null)
            attachmentRepository.getAttachment(response.thumbnailId).last() else null
        val thumbnailUrl = if (atmState is ResourceState.Success)
            atmState.data.url else null

        return Project(
            id = response.id,
            thumbnail = if (thumbnailUrl != null) Uri.parse(thumbnailUrl) else null,
            name = response.name,
            description = response.description,
            startDate = response.startDate,
            endDate = response.endDate,
            owner = owner
        )
    }

    companion object {
        const val TAG = "ProjectPagingSource"
    }

}