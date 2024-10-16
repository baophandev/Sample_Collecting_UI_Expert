package com.application.data.repository

import android.net.Uri
import android.util.Log
import com.application.constant.ProjectQueryType
import com.application.constant.ProjectStatus
import com.application.data.datasource.IProjectService
import com.application.data.entity.Form
import com.application.data.entity.Project
import com.application.data.entity.Stage
import com.application.data.entity.request.CreateFormRequest
import com.application.data.entity.request.CreateProjectRequest
import com.application.data.entity.request.CreateStageRequest
import com.application.util.ResourceState
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
    private val cachedStages: MutableMap<String, Stage> = mutableMapOf()
    private val cachedForms: MutableMap<String, Form> = mutableMapOf()

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
            startDate = startDate,
            endDate = endDate,
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
            val ownerState = userRepository.getUser(ownerId).last()
            val owner = if (ownerState is ResourceState.Success)
                ownerState.data else UserRepository.DEFAULT_USER.copy()
            val projectId = projectService.createProject(body)
            val newProject = Project(
                id = projectId,
                thumbnail = thumbnail,
                name = name,
                description = description,
                startDate = startDate,
                endDate = endDate,
                owner = owner
            )
            cachedProjects[projectId] = newProject

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
        return flow<ResourceState<List<Project>>> {
            val projects =
                projectService.getAllProject(userId, query, status, pageNumber, pageSize)
                    .map {
                        val ownerState = userRepository.getUser(it.ownerId).last()
                        val owner = if (ownerState is ResourceState.Success)
                            ownerState.data else UserRepository.DEFAULT_USER.copy()
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
            cachedProjects.putAll(projects.map { Pair(it.id, it) })
            emit(ResourceState.Success(projects))
        }.catch {
            Log.e(TAG, it.message ?: "Unknown exception")
            Log.e(TAG, it.stackTraceToString())
            emit(ResourceState.Error(message = "Cannot get projects"))
        }
    }

    fun getProject(projectId: String): Flow<ResourceState<Project>> {
        if (cachedProjects.containsKey(projectId))
            return flowOf(ResourceState.Success(cachedProjects[projectId]!!))

        return flow<ResourceState<Project>> {
            val response = projectService.getProject(projectId)
            val ownerState = userRepository.getUser(response.ownerId).last()
            val owner = if (ownerState is ResourceState.Success)
                ownerState.data else UserRepository.DEFAULT_USER.copy()
            val atmState = if (response.thumbnailId != null)
                attachmentRepository.getAttachment(response.thumbnailId).last() else null
            val thumbnailUrl = if (atmState is ResourceState.Success)
                atmState.data.url else null
            val project = Project(
                id = response.id,
                thumbnail = if (thumbnailUrl != null) Uri.parse(thumbnailUrl) else null,
                name = response.name,
                description = response.description,
                startDate = response.startDate,
                endDate = response.endDate,
                owner = owner
            )
            emit(ResourceState.Success(project))
        }.catch {
            Log.e(TAG, it.message ?: "Unknown exception")
            Log.e(TAG, it.stackTraceToString())
            emit(ResourceState.Error(message = "Cannot get projects"))
        }
    }


    //STAGE
    /**
     * Create a new stage of project.
     * @param .
     */
    fun createStage(
        name: String,
        description: String? = null,
        startDate: String? = null,
        endDate: String? = null,
        formId: String,
        projectOwnerId: String,
    ): Flow<ResourceState<String>> {
        var body = CreateStageRequest(
            name = name,
            description = description,
            startDate = startDate,
            endDate = endDate,
            formId = formId,
            projectOwnerId = projectOwnerId
        )

        return flow<ResourceState<String>> {
            val stageId = projectService.createStage(body)
            val newStage = Stage(
                id = stageId,
                name = name,
                description = description,
                startDate = startDate,
                endDate = endDate,
                formId = formId,
                projectOwnerId = projectOwnerId,
            )
            cachedStages[stageId] = newStage
            emit(ResourceState.Success(stageId))
        }.catch { exception ->
            Log.e(TAG, exception.message ?: "Unknown exception")
            Log.e(TAG, exception.stackTraceToString())
            emit(ResourceState.Error(message = "Cannot create a new stage"))
        }
    }

    /**
     * Get all stages of project by projectId.
     * @param .
     */
    fun getAllStage(
        projectId: String,
        pageNumber: Int = 0,
        pageSize: Int = 6
    ): Flow<ResourceState<List<Stage>>> {
        return flow<ResourceState<List<Stage>>> {
            val stages = projectService.getAllStage(projectId, pageNumber, pageSize).map {
                Stage(
                    id = it.id,
                    name = it.name,
                    description = it.description,
                    startDate = it.startDate,
                    endDate = it.endDate,
                    formId = it.formId,
                    projectOwnerId = it.projectOwnerId
                )
            }
            cachedStages.putAll(stages.map { Pair(it.id, it) })
            emit(ResourceState.Success(stages))
        }.catch { exception ->
            Log.e(TAG, exception.message ?: "Unknown exception")
            Log.e(TAG, exception.stackTraceToString())
            emit(ResourceState.Error(message = "Cannot get all stages"))
        }
    }

    /**
     * Get a stage by stageId .
     * @param .
     */
    fun getStage(stageId: String):Flow<ResourceState<Stage>>{
        if (cachedStages.containsKey(stageId))
            return flowOf(ResourceState.Success(cachedStages[stageId]!!))

        return flow<ResourceState<Stage>> {
            val response = projectService.getStage(stageId)
            val stage = Stage(
                id = response.id,
                name = response.name,
                description = response.description,
                startDate = response.startDate,
                endDate = response.endDate,
                formId = response.formId,
                projectOwnerId = response.projectOwnerId
            )
            emit(ResourceState.Success(stage))

        }.catch { exception ->
            Log.e(TAG, exception.message ?: "Unknown exception")
            Log.e(TAG, exception.stackTraceToString())
            emit(ResourceState.Error(message = "Cannot get a stage"))
        }
    }


    //Form
    /**
     * Create a new form of project.
     * @param .
     */
    fun createForm(
        title: String,
        description: String? = null,
        projectOwnerId: String
    ): Flow<ResourceState<String>> {
        var body = CreateFormRequest(
            title = title,
            description = description,
            projectOwnerId = projectOwnerId
        )

        return flow<ResourceState<String>> {
            val formId = projectService.createForm(body)
            val newForm = Form(
                id = formId,
                title = title,
                description = description,
                projectOwnerId = projectOwnerId,
            )
            cachedForms[formId] = newForm
            emit(ResourceState.Success(formId))
        }.catch { exception ->
            Log.e(TAG, exception.message ?: "Unknown exception")
            Log.e(TAG, exception.stackTraceToString())
            emit(ResourceState.Error(message = "Cannot create a new form"))
        }
    }

    /**
     * Get all forms of a project by projectId.
     *
     * ```
     */
    fun getAllForm(
        projectId: String,
        pageNumber: Int = 0,
        pageSize: Int = 6
    ): Flow<ResourceState<List<Form>>> {
        return flow<ResourceState<List<Form>>> {
            val forms = projectService.getAllForm(projectId, pageNumber, pageSize).map {
                Form(
                    id = it.id,
                    title = it.title,
                    description = it.description,
                    projectOwnerId = it.projectOwnerId
                )
            }
            cachedForms.putAll(forms.map { Pair(it.id, it) })
            emit(ResourceState.Success(forms))
        }.catch { exception ->
            Log.e(TAG, exception.message ?: "Unknown exception")
            Log.e(TAG, exception.stackTraceToString())
            emit(ResourceState.Error(message = "Cannot create a new form"))
        }
    }
    /**
     * Get a form of a project by formId.
     *
     * ```
     */
    fun getForm(formId: String): Flow<ResourceState<Form>> {
        if (cachedForms.containsKey(formId))
            return flowOf(ResourceState.Success(cachedForms[formId]!!))

        return flow<ResourceState<Form>> {
            val response = projectService.getForm(formId)
            val form = Form(
                id = response.id,
                title = response.title,
                description = response.description,
                projectOwnerId = response.projectOwnerId,
            )
            emit(ResourceState.Success(form))
        }.catch { exception ->
            Log.e(TAG, exception.message ?: "Unknown exception")
            Log.e(TAG, exception.stackTraceToString())
            emit(ResourceState.Error(message = "Cannot get a form"))
        }
    }

    companion object {
        const val TAG = "ProjectRepository"
    }


    //Field
    /**
     * Create a new field of project.
     * @param .
     */

}
