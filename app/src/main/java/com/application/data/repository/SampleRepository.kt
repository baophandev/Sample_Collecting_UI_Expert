package com.application.data.repository

import android.net.Uri
import android.util.Log
import com.application.android.utility.state.ResourceState
import com.application.data.datasource.IProjectService
import com.application.data.entity.Answer
import com.application.data.entity.DynamicField
import com.application.data.entity.Field
import com.application.data.entity.Sample
import com.application.data.entity.request.CreateDynamicFieldRequest
import com.application.data.entity.request.CreateSampleRequest
import com.application.data.entity.request.UpsertAnswerRequest
import com.application.data.entity.response.SampleResponse
import com.application.data.repository.ProjectRepository.Companion.TAG
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import java.sql.Timestamp

class SampleRepository(
    private val projectService: IProjectService,
    private val attachmentRepository: AttachmentRepository,
    private val fieldRepository: FieldRepository,
) {
    private val cachedSamples: MutableMap<String, Sample> = mutableMapOf()

    /**
     * Create a new sample.
     */
    fun createSample(
        attachmentUri: Uri? = null,
        position: String,
        projectOwnerId: String,
        stageId: String,
        answers: List<UpsertAnswerRequest>,
        dynamicFields: List<CreateDynamicFieldRequest>,
    ): Flow<ResourceState<String>> {

        var body = CreateSampleRequest(
            position = position,
            projectOwnerId = projectOwnerId,
            stageId = stageId,
            answers = answers,
            dynamicFields = dynamicFields
        )

        return flow<ResourceState<String>> {
            attachmentUri?.let {
                val attachmentState = attachmentRepository.storeAttachment(it).last()
                if (attachmentState is ResourceState.Success)
                    body = body.copy(attachmentId = attachmentState.data)
                else throw Exception("Storing attachment got an exception.")
            }

            val sampleId = projectService.createSample(body)

            val newSample = getSample(sampleId).last()
            if (newSample is ResourceState.Success)
                cachedSamples[sampleId] = newSample.data

            emit(ResourceState.Success(sampleId))
        }.catch { exception ->
            Log.e(TAG, exception.message, exception)
            emit(ResourceState.Error(message = "Cannot create a new sample"))
        }
    }

    /**
     * Get a sample by sampleId.
     */
    fun getSample(sampleId: String, skipCached: Boolean = false): Flow<ResourceState<Sample>> {
        if (!skipCached && cachedSamples.containsKey(sampleId))
            return flowOf(ResourceState.Success(cachedSamples[sampleId]!!))

        return flow<ResourceState<Sample>> {
            val response = projectService.getSample(sampleId)
            val sample = mapResponseToSample(response)
            emit(ResourceState.Success(sample))
        }.catch {
            Log.e(TAG, it.message, it)
            emit(ResourceState.Error(message = "Cannot get sample"))
        }
    }

    /**
     * Get all samples of a stage by stageId.
     */
    fun getAllSamplesOfStage(
        stageId: String,
        pageNumber: Int = 0,
        pageSize: Int = 6
    ): Flow<ResourceState<List<Sample>>> {
        return flow<ResourceState<List<Sample>>> {
            val samples =
                projectService.getAllSamplesOfStage(stageId, pageNumber, pageSize)
                    .map { mapResponseToSample(it) }
            cachedSamples.putAll(samples.map { Pair(it.id, it) })
            emit(ResourceState.Success(samples))
        }.catch {
            Log.e(TAG, it.message, it)
            emit(ResourceState.Error(message = "Cannot all samples of stage"))
        }
    }

    /**
     * Get all samples of a project by projectId.
     */
    fun getAllSamplesOfProject(
        projectId: String,
        pageNumber: Int = 0,
        pageSize: Int = 6
    ): Flow<ResourceState<List<Sample>>> {
        return flow<ResourceState<List<Sample>>> {
            val samples =
                projectService.getAllSamplesOfProject(projectId, pageNumber, pageSize)
                    .map { mapResponseToSample(it) }
            cachedSamples.putAll(samples.map { Pair(it.id, it) })
            emit(ResourceState.Success(samples))
        }.catch {
            Log.e(TAG, it.message, it)
            emit(ResourceState.Error(message = "Cannot all samples of project"))
        }
    }

    private suspend fun mapResponseToSample(response: SampleResponse): Sample {
        val atmState = if (response.attachmentId != null)
            attachmentRepository.getAttachment(response.attachmentId).last() else null
        val attachment = if (atmState is ResourceState.Success)
            atmState.data.url else null
        val image = if (attachment != null) Uri.parse(attachment) else null

        val answers = response.answers.map {
            val resourceState = fieldRepository.getField(it.field.id).last()
            val field = if (resourceState is ResourceState.Success)
                resourceState.data else Field.ERROR_FIELD
            Answer(content = it.value, field = field)
        }

        val dynamicFields = response.dynamicFields.map {
            DynamicField(
                id = it.id,
                name = it.name,
                value = it.value,
                numberOrder = it.numberOrder,
            )
        }

        return Sample(
            id = response.id,
            image = image,
            position = response.position,
            createdAt = Timestamp(response.createdAt),
            projectId = response.projectOwnerId,
            stageId = response.stageId,
            answers = answers,
            dynamicFields = dynamicFields,
        )
    }
}