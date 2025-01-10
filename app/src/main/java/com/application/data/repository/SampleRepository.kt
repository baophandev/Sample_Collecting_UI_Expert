package com.application.data.repository

import android.net.Uri
import android.util.Log
import com.application.R
import com.application.data.datasource.IProjectService
import com.application.data.entity.Answer
import com.application.data.entity.DynamicField
import com.application.data.entity.Field
import com.application.data.entity.Sample
import com.application.data.entity.request.CreateDynamicFieldRequest
import com.application.data.entity.request.CreateSampleRequest
import com.application.data.entity.request.UpsertAnswerRequest
import com.application.data.entity.response.SampleResponse
import com.application.data.exception.SampleException
import com.application.data.repository.ProjectRepository.Companion.TAG
import io.github.nhatbangle.sc.attachment.repository.AttachmentRepository
import io.github.nhatbangle.sc.utility.client.response.PagingResponse
import io.github.nhatbangle.sc.utility.state.ResourceState
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.runBlocking
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
    ): Flow<ResourceState<String>> = flow<ResourceState<String>> {
        val attachmentId =
            when (val rsState = attachmentRepository.storeAttachment(attachmentUri).last()) {
                is ResourceState.Success -> rsState.data
                is ResourceState.Error -> throw SampleException
                    .AttachmentStoringException("Storing attachment got an exception.")
            }
        val body = CreateSampleRequest(
            attachmentId = attachmentId,
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

        val sampleId = projectService.createSample(body)
        emit(ResourceState.Success(sampleId))
    }.catch { exception ->
        Log.e(TAG, exception.message, exception)
        emit(
            ResourceState.Error(
                message = "Cannot create a new sample",
                resId = R.string.create_sample_error
            )
        )
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
            cachedSamples[sampleId] = sample
            emit(ResourceState.Success(sample))
        }.catch {
            Log.e(TAG, it.message, it)
            emit(
                ResourceState.Error(
                    message = "Cannot get sample",
                    resId = R.string.get_sample_error
                )
            )
        }
    }

    fun deleteSample(sampleId: String): Flow<ResourceState<Boolean>> {
        return flow<ResourceState<Boolean>> {
            val response = projectService.deleteSample(sampleId)
            if (response) cachedSamples.remove(sampleId)
            emit(ResourceState.Success(response))
        }.catch {
            Log.e(TAG, it.message, it)
            emit(
                ResourceState.Error(
                    message = "Cannot delete a sample",
                    resId = R.string.error_delete_sample
                )
            )
        }
    }

    /**
     * Get all samples of a stage by stageId.
     */
    suspend fun getAllSamplesOfStage(
        stageId: String,
        pageNumber: Int = 0,
        pageSize: Int = 6
    ): Result<PagingResponse<Sample>> = runCatching {
        projectService.getAllSamplesOfStage(
            stageId = stageId,
            pageNumber = pageNumber,
            pageSize = pageSize
        ).map(::mapResponseToSample)
    }.onFailure { Log.e(TAG, it.message, it) }

    private fun mapResponseToSample(response: SampleResponse): Sample {
        val (image, answers, dynamicFields) = runBlocking {
            val image = async {
                when (val atmState =
                    attachmentRepository.getAttachment(response.attachmentId).last()) {
                    is ResourceState.Success -> Uri.parse(atmState.data.url)
                    is ResourceState.Error -> throw Exception("Cannot get a sample image.")
                }
            }
            val answers = response.answers.sortedBy { it.field.numberOrder }.map {
                async {
                    val resourceState = fieldRepository.getField(it.field.id).last()
                    val field = if (resourceState is ResourceState.Success)
                        resourceState.data else Field.ERROR_FIELD
                    Answer(content = it.value, field = field)
                }
            }
            val dynamicFields = response.dynamicFields.sortedBy { it.numberOrder }.map {
                DynamicField(
                    id = it.id,
                    name = it.name,
                    value = it.value,
                )
            }

            Triple(image.await(), answers.awaitAll(), dynamicFields)
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