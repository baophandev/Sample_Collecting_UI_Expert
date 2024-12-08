package com.application.data.repository

import android.net.Uri
import android.util.Log
import com.application.android.utility.client.response.PagingResponse
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
        attachmentUri: Uri,
        position: String,
        stageId: String,
        answers: List<Answer>,
        dynamicFields: List<DynamicField>,
    ): Flow<ResourceState<String>> {
        var body = CreateSampleRequest(
            position = position,
            stageId = stageId,
            answers = answers.map {
                UpsertAnswerRequest(fieldId = it.field.id, value = it.content)
            },
            dynamicFields = dynamicFields.mapIndexed { index, field ->
                CreateDynamicFieldRequest(
                    name = field.name,
                    value = field.value,
                    numberOrder = index
                )
            }
        )

        return flow<ResourceState<String>> {
            val attachmentState = attachmentRepository.storeAttachment(attachmentUri).last()
            if (attachmentState is ResourceState.Success)
                body = body.copy(attachmentId = attachmentState.data)
            else throw Exception("Storing attachment got an exception.")

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

    fun deleteSample(sampleId: String): Flow<ResourceState<Boolean>> {
        return flow<ResourceState<Boolean>> {
            val response = projectService.deleteSample(sampleId)
            emit(ResourceState.Success(response))
        }.catch {
            Log.e(TAG, it.message, it)
            emit(ResourceState.Error(message = "Cannot get sample"))
        }
    }

    /**
     * Get all samples of a stage by stageId.
     */
    suspend fun getAllSamplesOfStage(
        stageId: String,
        pageNumber: Int = 0,
        pageSize: Int = 6
    ): Result<PagingResponse<Sample>> {
        return runCatching {
            val response = projectService.getAllSamplesOfStage(stageId, pageNumber, pageSize)
            val samples = response.content.map { mapResponseToSample(it) }
            PagingResponse(
                totalPages = response.totalPages,
                totalElements = response.totalElements,
                number = response.number,
                size = response.size,
                numberOfElements = response.numberOfElements,
                first = response.first,
                last = response.last,
                content = samples
            )
        }.onFailure { Log.e(TAG, it.message, it) }
    }

    private suspend fun mapResponseToSample(response: SampleResponse): Sample {
        val atmState = attachmentRepository.getAttachment(response.attachmentId).last()
        val image = if (atmState is ResourceState.Success)
            Uri.parse(atmState.data.url)
        else throw Exception("Cannot get a sample image.")

        val answers = response.answers
            .sortedBy { it.field.numberOrder }
            .map {
                val resourceState = fieldRepository.getField(it.field.id).last()
                val field = if (resourceState is ResourceState.Success)
                    resourceState.data else Field.ERROR_FIELD
                Answer(content = it.value, field = field)
            }

        val dynamicFields = response.dynamicFields
            .sortedBy { it.numberOrder }
            .map {
                DynamicField(
                    id = it.id,
                    name = it.name,
                    value = it.value,
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