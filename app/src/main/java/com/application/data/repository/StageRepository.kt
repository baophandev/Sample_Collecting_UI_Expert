package com.application.data.repository

import android.util.Log
import com.application.data.datasource.IProjectService
import com.application.data.entity.Form
import com.application.data.entity.Stage
import com.application.data.entity.request.CreateStageRequest
import com.application.data.entity.response.StageResponse
import com.application.data.repository.ProjectRepository.Companion.TAG
import com.application.util.ResourceState
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
    fun getAllStages(
        projectId: String,
        pageNumber: Int = 0,
        pageSize: Int = 6
    ): Flow<ResourceState<List<Stage>>> {
        return flow<ResourceState<List<Stage>>> {
            val stages = projectService.getAllStages(projectId, pageNumber, pageSize)
                .content.map(this@StageRepository::mapResponseToStage)
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
    fun getStage(stageId: String): Flow<ResourceState<Stage>> {
        if (cachedStages.containsKey(stageId))
            return flowOf(ResourceState.Success(cachedStages[stageId]!!))

        return flow<ResourceState<Stage>> {
            val response = projectService.getStage(stageId)
            val stage = mapResponseToStage(response)
            emit(ResourceState.Success(stage))
        }.catch { exception ->
            Log.e(TAG, exception.message, exception)
            emit(ResourceState.Error(message = "Cannot get a stage"))
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