package com.application.data.repository

import android.net.Uri
import android.util.Log
import com.application.R
import com.application.constant.ProjectQueryType
import com.application.constant.ProjectStatus
import com.application.data.datasource.IProjectService
import com.application.data.entity.Project
import com.application.data.entity.request.CreateProjectRequest
import com.application.data.entity.request.UpdateProjectRequest
import com.application.data.entity.response.ProjectResponse
import com.application.data.exception.ProjectException
import com.sc.library.attachment.repository.AttachmentRepository
import com.sc.library.user.repository.UserRepository
import com.sc.library.utility.client.response.PagingResponse
import com.sc.library.utility.state.ResourceState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.runBlocking

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
                when (val attachmentState = attachmentRepository.storeAttachment(it).last()) {
                    is ResourceState.Error -> throw Exception("Storing attachment got an exception.")
                    is ResourceState.Success -> body = body.copy(thumbnailId = attachmentState.data)
                }
            }
            val projectId = projectService.createProject(body)
            emit(ResourceState.Success(projectId))
        }.catch { exception ->
            Log.e(TAG, exception.message, exception)
            emit(
                ResourceState.Error(
                    message = "Cannot create a new project",
                    resId = R.string.create_project_error
                )
            )
        }
    }

    fun getProject(projectId: String, skipCached: Boolean = false): Flow<ResourceState<Project>> {
        if (!skipCached && cachedProjects.containsKey(projectId))
            return flowOf(ResourceState.Success(cachedProjects[projectId]!!))

        return flow<ResourceState<Project>> {
            val response = projectService.getProject(projectId)
            val project = mapResponseToProject(response)

            cachedProjects[projectId] = project
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

    suspend fun getAllProjects(
        userId: String,
        query: ProjectQueryType = ProjectQueryType.ALL,
        status: ProjectStatus = ProjectStatus.NORMAL,
        pageNumber: Int = 0,
        pageSize: Int = 6
    ): Result<PagingResponse<Project>> = runCatching {
        val response = projectService.getAllProjects(
            userId = userId,
            query = query,
            status = status,
            pageNumber = pageNumber,
            pageSize = pageSize
        )
        val projects = response.content.map { mapResponseToProject(it) }
        PagingResponse(
            totalPages = response.totalPages,
            totalElements = response.totalElements,
            number = response.number,
            size = response.size,
            numberOfElements = response.numberOfElements,
            first = response.first,
            last = response.last,
            content = projects
        )
    }.onFailure { Log.e(TAG, it.message, it) }

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
                else throw ProjectException
                    .AttachmentStoringException("Storing attachment got an exception.")
            }

            val updateResult = projectService.updateProject(projectId, updateRequest)
            // get updated project from server
            if (updateResult) getProject(projectId, true)

            emit(ResourceState.Success(true))
        }.catch { exception ->
            Log.e(TAG, exception.message, exception)
            emit(
                ResourceState.Error(
                    message = "Cannot update projects",
                    resId = R.string.error_modify_project
                )
            )
        }
    }


    fun deleteProject(
        projectId: String
    ): Flow<ResourceState<Boolean>> {
        return flow<ResourceState<Boolean>> {
            val deleteResult = projectService.deleteProject(projectId = projectId)
            if (deleteResult) emit(ResourceState.Success(true))
        }.catch { exception ->
            Log.e(TAG, exception.message, exception)
            emit(
                ResourceState.Error(
                    message = "Cannot delete project",
                    resId = R.string.delete_project_error
                )
            )
        }
    }

    /**
     * @throws [CancellationException]
     */
    private suspend fun mapResponseToProject(response: ProjectResponse): Project {
        val (thumbnail, owner, members) = runBlocking {
            val thumbnail = async {
                val atmState = response.thumbnailId?.let {
                    if (it.isBlank()) return@let null
                    attachmentRepository.getAttachment(response.thumbnailId).last()
                }
                when (atmState) {
                    is ResourceState.Success -> Uri.parse(atmState.data.url)
                    else -> null
                }
            }

            val owner = async {
                when (val rsState = userRepository.getUser(response.ownerId).last()) {
                    is ResourceState.Error -> throw Exception("Cannot get project owner data.")
                    is ResourceState.Success -> rsState.data
                }
            }

            val members = response.memberIds?.map {
                async {
                    when (val rsState = userRepository.getUser(it).last()) {
                        is ResourceState.Error -> throw Exception("Cannot get project member data.")
                        is ResourceState.Success -> rsState.data
                    }
                }
            }

            Triple(
                thumbnail.await(),
                owner.await(),
                members?.awaitAll()
            )
        }

        return Project(
            id = response.id,
            thumbnail = thumbnail,
            name = response.name,
            description = response.description,
            startDate = response.startDate,
            endDate = response.endDate,
            owner = owner,
            members = members ?: emptyList()
        )
    }

    companion object {
        const val TAG = "ProjectRepository"
    }

}
