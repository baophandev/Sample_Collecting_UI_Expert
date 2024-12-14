package com.application.data.repository

import android.util.Log
import com.application.R
import com.application.data.datasource.IProjectService
import com.application.data.entity.Stage
import com.application.data.entity.request.CreateStageRequest
import com.application.data.entity.request.UpdateStageRequest
import com.application.data.entity.response.StageResponse
import com.application.data.repository.ProjectRepository.Companion.TAG
import com.sc.library.utility.client.response.PagingResponse
import com.sc.library.utility.state.ResourceState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class StageRepository(
    private val projectService: IProjectService,
) {
    private val cachedStages: MutableMap<String, Stage> = mutableMapOf()

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
        val body = CreateStageRequest(
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
    suspend fun getAllStages(
        projectId: String,
        pageNumber: Int = 0,
        pageSize: Int = 6
    ): Result<PagingResponse<Stage>> = runCatching {
        val response = projectService.getAllStages(
            projectId = projectId,
            pageNumber = pageNumber,
            pageSize = pageSize
        )
        val stages = response.content.map { mapResponseToStage(it) }
        PagingResponse(
            totalPages = response.totalPages,
            totalElements = response.totalElements,
            number = response.number,
            size = response.size,
            numberOfElements = response.numberOfElements,
            first = response.first,
            last = response.last,
            content = stages
        )
    }.onFailure { Log.e(TAG, it.message, it) }

    /**
     * Get a stage by stageId .
     * @param .
     */
    fun getStage(stageId: String, skipCached: Boolean = false): Flow<ResourceState<Stage>> {
        if (!skipCached && cachedStages.containsKey(stageId))
            return flowOf(ResourceState.Success(cachedStages[stageId]!!))

        return flow<ResourceState<Stage>> {
            val response = projectService.getStage(stageId)
            val stage = mapResponseToStage(response)
            emit(ResourceState.Success(stage))
        }.catch { exception ->
            Log.e(TAG, exception.message, exception)
            emit(
                ResourceState.Error(
                    message = "Cannot get a stage",
                    resId = R.string.error_cannot_get_stage
                )
            )
        }
    }

    fun updateStage(
        stageId: String,
        name: String? = null,
        description: String? = null,
        startDate: String? = null,
        endDate: String? = null,
        formId: String? = null,
    ): Flow<ResourceState<Boolean>> {
        val updateRequest = UpdateStageRequest(
            name = name,
            description = description,
            startDate = startDate,
            endDate = endDate,
            formId = formId
        )
        return flow<ResourceState<Boolean>> {

            val updateResult = projectService.updateStage(stageId, updateRequest)
            // get updated project from server
            if (updateResult) getStage(stageId, true)

            emit(ResourceState.Success(true))
        }.catch { exception ->
            Log.e(TAG, exception.message, exception)
            emit(ResourceState.Error(message = "Cannot update stages"))
        }
    }

    fun deleteStage(
        stageId: String
    ): Flow<ResourceState<Boolean>> {
        return flow<ResourceState<Boolean>> {
            val deleteResult = projectService.deleteStage(stageId = stageId)
            if (deleteResult) emit(ResourceState.Success(true))
        }.catch { exception ->
            Log.e(TAG, exception.message, exception)
            emit(ResourceState.Error(message = "Cannot delete project"))
        }
    }

    private fun mapResponseToStage(response: StageResponse): Stage {
        return Stage(
            id = response.id,
            name = response.name,
            description = response.description,
            startDate = response.startDate,
            endDate = response.endDate,
            formId = response.formId,
            projectOwnerId = response.projectOwnerId,
        )
    }

}