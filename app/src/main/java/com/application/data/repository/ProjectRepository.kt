package com.application.data.repository

import android.net.Uri
import android.util.Log
import com.application.constant.ProjectQueryType
import com.application.constant.ProjectStatus
import com.application.data.datasource.IProjectService
import com.application.data.entity.Project
import com.application.data.entity.User
import com.application.data.entity.request.CreateProjectRequest
import com.application.util.ResourceState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.last

class ProjectRepository(
    private val projectService: IProjectService,
    private val userRepository: UserRepository,
    private val attachmentRepository: AttachmentRepository
) {

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
        ownerId: String,
        memberIds: List<String>? = null
    ): Flow<ResourceState<String>> {

        var body = CreateProjectRequest(
            name = name,
            description = description,
            startDate = startDate?.toString(),
            endDate = endDate?.toString(),
            ownerId = ownerId,
            memberIds = memberIds
        )

        return flow<ResourceState<String>> {
            thumbnail?.let {
                val attachmentState = attachmentRepository.storeAttachment(it).last()
                if (attachmentState is ResourceState.Success)
                    body = body.copy(thumbnailId = attachmentState.data)
                else throw Exception("Storing attachment got an exception.")
            }

            val projectId = projectService.createProject(body)
            emit(ResourceState.Success(projectId))
        }.catch { exception ->
            Log.e(TAG, exception.message ?: "Unknown exception")
            Log.e(TAG, exception.stackTraceToString())
            emit(ResourceState.Error(message = "Cannot create a new project"))
        }
    }

    /**
     * Get all projects of a user.
     *
     * If project owner information cannot be fetched from server, default user information will be used.
     * ```
     * val defaultUser = User(id = "unknown", username = "unknown", name = "Unknown User")
     * ```
     */
    fun getAllProject(
        userId: String,
        query: ProjectQueryType = ProjectQueryType.ALL,
        status: ProjectStatus = ProjectStatus.NORMAL,
        pageNumber: Int = 0,
        pageSize: Int = 6
    ): Flow<ResourceState<List<Project>>> {
        val defaultUser = User(id = "unknown", username = "unknown", name = "Unknown User")

        return flow<ResourceState<List<Project>>> {
            val projects =
                projectService.getAllProject(userId, query, status, pageNumber, pageSize)
                    .map {
                        val ownerState = userRepository.getUser(it.ownerId).last()
                        val owner = if (ownerState is ResourceState.Success)
                            ownerState.data else defaultUser.copy()
                        val atmState = if (it.thumbnailId != null)
                            attachmentRepository.getAttachment(it.thumbnailId).last() else null
                        val thumbnailUrl = if (atmState is ResourceState.Success)
                            atmState.data.url else null

                        Project(
                            id = it.id,
                            thumbnail = if (thumbnailUrl != null) Uri.parse(thumbnailUrl) else null,
                            name = it.name,
                            description = it.description,
                            startDate = it.startDate,
                            endDate = it.endDate,
                            owner = owner
                        )
                    }
            emit(ResourceState.Success(projects))
        }.catch {
            Log.e(TAG, it.message ?: "Unknown exception")
            Log.e(TAG, it.stackTraceToString())
            emit(ResourceState.Error(message = "Cannot get projects"))
        }
    }

    companion object {
        const val TAG = "ProjectRepository"
    }

}
