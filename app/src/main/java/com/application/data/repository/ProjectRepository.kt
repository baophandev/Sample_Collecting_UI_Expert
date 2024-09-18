package com.application.data.repository

import android.util.Log
import com.application.data.datasource.IProjectService
import com.application.data.entity.request.CreateProjectRequest
import com.application.util.ResourceState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import java.util.Date

class ProjectRepository(
    private val service: IProjectService
) {

    fun createProject(
        name: String,
        description: String? = null,
        startDate: Date? = null,
        endDate: Date? = null,
        ownerId: String,
        memberIds: List<String>? = null
    ) : Flow<ResourceState<String>> {
        val body = CreateProjectRequest(name, description, "2024-09-16", "2024-09-16", ownerId, memberIds)
        return flow<ResourceState<String>> {
            val projectId = service.createProject(body)
            emit(ResourceState.Success(projectId))
        }.catch { exception ->
            Log.e(TAG, exception.message ?: "Unknown exception")
            emit(ResourceState.Error(str = "Cannot create a new project"))
        }
    }

    companion object {
        const val TAG = "ProjectRepository"
    }

}
