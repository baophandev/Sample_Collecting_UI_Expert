package com.application.data.paging

import android.net.Uri
import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.application.android.utility.state.ResourceState
import com.application.data.datasource.IProjectService
import com.application.data.entity.Project
import com.application.data.entity.response.ProjectResponse
import com.application.data.repository.AttachmentRepository
import com.application.data.repository.UserRepository
import io.ktor.client.plugins.ClientRequestException
import kotlinx.coroutines.flow.last
import java.io.IOException
import java.lang.reflect.Field

class FieldPagingSource(
    private val projectService: IProjectService,
    private val userRepository: UserRepository,
    private val attachmentRepository: AttachmentRepository
) : PagingSource<Int, Field>() {

    override fun getRefreshKey(state: PagingState<Int, Field>): Int? {
        // We need to get the previous key (or next key if previous is null) of the page
        // that was closest to the most recently accessed index.
        // Anchor position is the most recently accessed index
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Field> {
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
            val nextKey = if (!response.last) nextPageNumber + 1 else null
            return LoadResult.Page(
                data = listOf(),
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