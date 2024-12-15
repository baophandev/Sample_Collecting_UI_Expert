package com.application.data.repository

import android.util.Log
import com.application.R
import com.application.data.datasource.IProjectService
import com.application.data.entity.Form
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
     */
    fun createStage(
        name: String,
        description: String? = null,
        startDate: String? = null,
        endDate: String? = null,
        form: Form,
    ): Flow<ResourceState<String>> = flow<ResourceState<String>> {
        val body = CreateStageRequest(
            name = name,
            description = description,
            startDate = startDate,
            endDate = endDate,
            formId = form.id,
            projectOwnerId = form.projectOwnerId
        )
        val stageId = projectService.createStage(body)
        emit(ResourceState.Success(stageId))
    }.catch { exception ->
        Log.e(TAG, exception.message, exception)
        emit(
            ResourceState.Error(
                message = "Cannot create a new stage.",
                resId = R.string.create_stage_error
            )
        )
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
            cachedStages[stageId] = stage
            emit(ResourceState.Success(stage))
        }.catch { exception ->
            Log.e(TAG, exception.message, exception)
            emit(
                ResourceState.Error(
                    message = "Cannot get a stage.",
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
    ): Flow<ResourceState<Boolean>> = flow<ResourceState<Boolean>> {
        val updateRequest = UpdateStageRequest(
            name = name,
            description = description,
            startDate = startDate,
            endDate = endDate,
            formId = formId
        )
        val updateResult = projectService.updateStage(stageId, updateRequest)
        emit(ResourceState.Success(updateResult))
    }.catch { exception ->
        Log.e(TAG, exception.message, exception)
        emit(
            ResourceState.Error(
                message = "Cannot update a stage.",
                resId = R.string.update_stage_error
            )
        )
    }

    fun deleteStage(
        stageId: String
    ): Flow<ResourceState<Boolean>> {
        return flow<ResourceState<Boolean>> {
            val deleteResult = projectService.deleteStage(stageId = stageId)
            if (deleteResult) cachedStages.remove(stageId)
            emit(ResourceState.Success(deleteResult))
        }.catch { exception ->
            Log.e(TAG, exception.message, exception)
            emit(
                ResourceState.Error(
                    message = "Cannot delete a stage.",
                    resId = R.string.delete_stage_error
                )
            )
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