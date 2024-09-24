package com.application.data.repository

import android.util.Log
import com.application.data.datasource.IProjectService
import com.application.data.entity.Project
import com.application.data.entity.User
import com.application.data.entity.request.CreateProjectRequest
import com.application.util.ResourceState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import java.time.LocalDate

class ProjectRepository(
    private val service: IProjectService
) {

    fun createProject(
        name: String,
        description: String? = null,
        startDate: LocalDate? = null,
        endDate: LocalDate? = null,
        ownerId: String,
        memberIds: List<String>? = null
    ): Flow<ResourceState<String>> {
        val body = CreateProjectRequest(
            name,
            description,
            startDate,
            endDate,
            ownerId,
            memberIds
        )
        return flow<ResourceState<String>> {
            val projectId = service.createProject(body)
            emit(ResourceState.Success(projectId))
        }.catch { exception ->
            Log.e(TAG, exception.message ?: "Unknown exception")
            emit(ResourceState.Error(str = "Cannot create a new project"))
        }
    }

    fun getAllProject(
        userId: String,
        isOwner: Boolean = true,
        status: String = "NORMAL",
        pageNumber: Int = 0,
        pageSize: Int = 6
    ): Flow<ResourceState<List<Project>>> {
        return flow<ResourceState<List<Project>>> {
            val projects = service.getAllProject(userId, isOwner, status, pageNumber, pageSize)
                .map {
                    Project(
                        id = it.id,
                        thumbnailUri = null,
                        name = it.name,
                        description = it.description,
                        startDate = it.startDate?.toEpochDay(),
                        endDate = it.endDate?.toEpochDay(),
                        owner = User(id = userId, username = "", name = "")
                    )
                }

            emit(ResourceState.Success(projects))
        }.catch {
            Log.e(TAG, it.message ?: "Unknown exception")
            emit(ResourceState.Error(str = "Cannot get projects"))
        }
    }

    companion object {
        const val TAG = "ProjectRepository"
    }

}
