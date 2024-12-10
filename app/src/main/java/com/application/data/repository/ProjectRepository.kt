package com.application.data.repository

import android.net.Uri
import android.util.Log
import com.application.R
import com.application.android.user_library.repository.UserRepository
import com.application.android.utility.state.ResourceState
import com.application.data.datasource.IProjectService
import com.application.data.entity.Project
import com.application.data.entity.request.CreateProjectRequest
import com.application.data.entity.request.UpdateProjectRequest
import com.application.data.entity.response.ProjectResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last

class ProjectRepository(
    private val projectService: IProjectService,
    private val userRepository: UserRepository,
    private val attachmentRepository: AttachmentRepository
) {
    private val cachedProjects: MutableMap<String, Project> = mutableMapOf()

    /**
     * Create a new project.
     * @param thumbnail If it is not uploaded to server successfully, the creating process will be failed.
     */
    fun createProject(
        thumbnail: Uri? = null,
        name: String,
        description: String? = null,
        startDate: String? = null,
        endDate: String? = null,
        memberIds: List<String>? = null
    ): Flow<ResourceState<String>> {
        return flow<ResourceState<String>> {
            val ownerId = userRepository.loggedUser?.id
                ?: throw Exception("Cannot get logged user ID.")
            var body = CreateProjectRequest(
                name = name,
                description = description,
                startDate = startDate,
                endDate = endDate,
                ownerId = ownerId,
                memberIds = memberIds
            )

            thumbnail?.let {
                val attachmentState = attachmentRepository.storeAttachment(it).last()
                if (attachmentState is ResourceState.Success)
                    body = body.copy(thumbnailId = attachmentState.data)
                else throw Exception("Storing attachment got an exception.")
            }
            val projectId = projectService.createProject(body)

            val newProject = getProject(projectId).last()
            if (newProject is ResourceState.Success)
                cachedProjects[projectId] = newProject.data

            emit(ResourceState.Success(projectId))
        }.catch { exception ->
            Log.e(TAG, exception.message, exception)
            emit(ResourceState.Error(message = "Cannot create a new project"))
        }
    }

    fun getProject(projectId: String, skipCached: Boolean = false): Flow<ResourceState<Project>> {
        if (!skipCached && cachedProjects.containsKey(projectId))
            return flowOf(ResourceState.Success(cachedProjects[projectId]!!))

        return flow<ResourceState<Project>> {
            val response = projectService.getProject(projectId)
            val project = mapResponseToProject(response)
            emit(ResourceState.Success(project))
        }.catch {
            Log.e(TAG, it.message, it)
            emit(
                ResourceState.Error(
                    message = "Cannot get projects",
                    resId = R.string.error_cannot_get_project
                )
            )
        }
    }

    fun updateProject(
        projectId: String,
        thumbnail: Uri? = null,
        name: String? = null,
        description: String? = null,
        startDate: String? = null,
        endDate: String? = null,
    ): Flow<ResourceState<Boolean>> {
        var updateRequest = UpdateProjectRequest(
            name = name,
            description = description,
            startDate = startDate,
            endDate = endDate
        )
        return flow<ResourceState<Boolean>> {
            thumbnail?.let {
                val attachmentState = attachmentRepository.storeAttachment(it).last()
                if (attachmentState is ResourceState.Success)
                    updateRequest = updateRequest.copy(thumbnailId = attachmentState.data)
                else throw Exception("Storing attachment got an exception.")
            }

            val updateResult = projectService.updateProject(projectId, updateRequest)
            // get updated project from server
            if (updateResult) getProject(projectId, true)

            emit(ResourceState.Success(true))
        }.catch { exception ->
            Log.e(TAG, exception.message, exception)
            emit(ResourceState.Error(message = "Cannot update projects"))
        }
    }

    private suspend fun mapResponseToProject(response: ProjectResponse): Project {
        val ownerState = userRepository.getUser(response.ownerId).last()
        val owner = if (ownerState is ResourceState.Success)
            ownerState.data else throw Exception("Cannot get project owner data.")
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

    fun deleteProject(
        projectId: String
    ): Flow<ResourceState<Boolean>> {
        return flow<ResourceState<Boolean>> {
            val deleteResult = projectService.deleteProject(projectId = projectId)
            if (deleteResult) emit(ResourceState.Success(true))
        }.catch { exception ->
            Log.e(TAG, exception.message, exception)
            emit(ResourceState.Error(message = "Cannot delete project"))
        }
    }

    companion object {
        const val TAG = "ProjectRepository"
    }

}
