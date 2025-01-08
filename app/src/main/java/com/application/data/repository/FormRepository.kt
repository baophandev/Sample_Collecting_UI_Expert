package com.application.data.repository

import android.util.Log
import com.application.R
import com.application.data.datasource.IProjectService
import com.application.data.entity.Form
import com.application.data.entity.request.CreateFormRequest
import com.application.data.entity.request.UpdateFormRequest
import com.application.data.entity.response.FormResponse
import com.application.data.exception.FormException
import com.application.data.repository.ProjectRepository.Companion.TAG
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

class FormRepository(
    private val projectService: IProjectService,
    private val fieldRepository: FieldRepository
) {
    private val cachedForms: MutableMap<String, Form> = mutableMapOf()

    /**
     * Get all forms of a project by projectId.
     *
     */
    suspend fun getAllForms(
        projectId: String,
        pageNumber: Int = 0,
        pageSize: Int = 6
    ): Result<PagingResponse<Form>> = runCatching {
        projectService.getAllForms(
            projectId = projectId,
            pageNumber = pageNumber,
            pageSize = pageSize
        ).map(::mapResponseToForm)
    }.onFailure { Log.e(TAG, it.message, it) }

    /**
     * Get a form of a project by formId.
     *
     */
    fun getForm(
        formId: String,
        skipCached: Boolean = false
    ): Flow<ResourceState<Form>> {
        if (!skipCached && cachedForms.containsKey(formId))
            return flowOf(ResourceState.Success(cachedForms[formId]!!))

        return flow<ResourceState<Form>> {
            val response = projectService.getForm(formId)
            val form = mapResponseToForm(response)
            cachedForms[formId] = form
            emit(ResourceState.Success(form))
        }.catch { exception ->
            Log.e(TAG, exception.message, exception)
            emit(
                ResourceState.Error(
                    message = "Cannot get a form",
                    resId = R.string.error_cannot_get_form
                )
            )
        }
    }

    /**
     * Create a new form of project.
     */
    fun createForm(
        title: String,
        description: String? = null,
        projectOwnerId: String,
        fields: List<String>
    ): Flow<ResourceState<String>> = flow<ResourceState<String>> {
        val body = CreateFormRequest(
            title = title,
            description = description,
            projectOwnerId = projectOwnerId
        )
        val formId = projectService.createForm(body)
        runBlocking {
            fields.mapIndexed { index, fieldName ->
                async {
                    val rsState = fieldRepository.createField(
                        formId = formId,
                        name = fieldName,
                        numberOrder = index
                    ).last()
                    if (rsState is ResourceState.Error) throw FormException
                        .FieldCreatingException("Cannot create field with name: $fieldName")
                }
            }.awaitAll()
        }
        val newForm = Form(
            id = formId,
            title = title,
            description = description,
            projectOwnerId = projectOwnerId,
        )
        cachedForms[formId] = newForm
        emit(ResourceState.Success(formId))
    }.catch { exception ->
        Log.e(TAG, exception.message, exception)
        emit(
            ResourceState.Error(
                message = "Cannot create a new form",
                resId = R.string.create_form_error
            )
        )
    }

    /**
     * Delete a form of stage.
     */
    fun deleteForm(formId: String): Flow<ResourceState<Boolean>> = flow<ResourceState<Boolean>> {
        val deleteResult = projectService.deleteForm(formId = formId)
        if (deleteResult) cachedForms.remove(formId)
        emit(ResourceState.Success(deleteResult))
    }.catch { exception ->
        Log.e(TAG, exception.message, exception)
        emit(
            ResourceState.Error(
                message = "Cannot delete form",
                resId = R.string.delete_form_error
            )
        )
    }

    /**
     * Update a form of project.
     */
    fun updateForm(
        formId: String,
        title: String,
        description: String? = null
    ): Flow<ResourceState<Boolean>> = flow<ResourceState<Boolean>> {
        val updateRequest = UpdateFormRequest(
            title = title,
            description = description
        )

        val updateResult =
            projectService.updateForm(formId = formId, updateRequestData = updateRequest)
        emit(ResourceState.Success(updateResult))
    }.catch { exception ->
        Log.e(TAG, exception.message, exception)
        emit(
            ResourceState.Error(
                message = "Cannot update form",
                resId = R.string.update_form_error
            )
        )
    }


    private fun mapResponseToForm(response: FormResponse): Form {
        return Form(
            id = response.id,
            title = response.title,
            description = response.description,
            projectOwnerId = response.projectOwnerId
        )
    }

}