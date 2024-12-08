package com.application.data.repository

import android.util.Log
import com.application.data.datasource.IProjectService
import com.application.data.entity.Form
import com.application.data.entity.request.CreateFormRequest
import com.application.data.entity.request.UpdateFormRequest
import com.application.data.entity.response.FormResponse
import com.application.data.repository.ProjectRepository.Companion.TAG
import com.application.android.utility.state.ResourceState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last

class FormRepository(
    private val projectService: IProjectService,
    private val fieldRepository: FieldRepository
) {
    private val cachedForms: MutableMap<String, Form> = mutableMapOf()

    /**
     * Get all forms of a project by projectId.
     *
     */
    fun getAllForms(
        projectId: String,
        pageNumber: Int = 0,
        pageSize: Int = 6
    ): Flow<ResourceState<List<Form>>> {
        return flow<ResourceState<List<Form>>> {
            val forms = projectService.getAllForms(projectId, pageNumber, pageSize)
                .content.map(this@FormRepository::mapResponseToForm)
            cachedForms.putAll(forms.map { Pair(it.id, it) })
            emit(ResourceState.Success(forms))
        }.catch { exception ->
            Log.e(TAG, exception.message, exception)
            emit(ResourceState.Error(message = "Cannot get all forms"))
        }
    }

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
            emit(ResourceState.Success(form))
        }.catch { exception ->
            Log.e(TAG, exception.message, exception)
            emit(ResourceState.Error(message = "Cannot get a form"))
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
    ): Flow<ResourceState<String>> {
        val body = CreateFormRequest(
            title = title,
            description = description,
            projectOwnerId = projectOwnerId
        )

        return flow<ResourceState<String>> {
            val formId = projectService.createForm(body)
            fields.forEachIndexed { index, fieldName ->
                val resourceState = fieldRepository.createField(
                    formId = formId,
                    name = fieldName,
                    numberOrder = index
                ).last()
                if (resourceState is ResourceState.Error)
                    throw Exception("Cannot create field with name: $fieldName")
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
            emit(ResourceState.Error(message = "Cannot create a new form"))
        }
    }

    /**
     * Delete a form of stage.
     */
    fun deleteForm(
        formId: String
    ): Flow<ResourceState<Boolean>> {
        return flow<ResourceState<Boolean>>{
            val deleteResult = projectService.deleteForm(formId = formId)
            if (deleteResult) emit(ResourceState.Success(true))
        }.catch { exception ->
            Log.e(TAG, exception.message, exception)
            emit(ResourceState.Error(message = "Cannot delete form"))
        }
    }

    /**
     * Update a form of project.
     */
    fun updateForm(
        formId: String,
        title: String,
        description: String? = null
    ): Flow<ResourceState<Boolean>> {
        val updateRequest = UpdateFormRequest(
            title = title,
            description = description
        )
        return flow<ResourceState<Boolean>> {
            val updateResult =
                projectService.updateForm(formId = formId, updateRequestData = updateRequest)
            // get updated form from server
            if (updateResult) getForm(formId = formId, skipCached = true)
            emit(ResourceState.Success(true))
        }.catch { exception ->
            Log.e(TAG, exception.message, exception)
            emit(ResourceState.Error(message = "Cannot update form"))
        }
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