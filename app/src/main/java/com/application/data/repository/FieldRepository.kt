package com.application.data.repository

import android.util.Log
import com.application.data.datasource.IProjectService
import com.application.data.entity.Field
import com.application.data.entity.request.CreateFieldRequest
import com.application.data.entity.response.FieldResponse
import com.application.util.ResourceState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import com.application.data.repository.ProjectRepository.Companion.TAG
import kotlinx.coroutines.flow.flowOf

class FieldRepository(
    private val projectService: IProjectService,
){
    private val cachedFields: MutableMap<String, Field> = mutableMapOf()

    //Field
    /**
     * Get all field of a form by formId.
     *
     */
    fun getAllFields(
        formId: String,
        pageNumber: Int = 0,
        pageSize: Int = 6
    ): Flow<ResourceState<List<Field>>> {
        return flow<ResourceState<List<Field>>> {
//            val fields = projectService.getAllField(formId, pageNumber, pageSize)
//                .content.map(this@FieldRepository::mapResponseToField)           
            val fields = projectService.getAllField(formId, pageNumber, pageSize)
                .map(this@FieldRepository::mapResponseToField)
            cachedFields.putAll(fields.map { Pair(it.id, it) })
            emit(ResourceState.Success(fields))
        }.catch { exception ->
            Log.e(TAG, exception.message ?: "Unknown exception")
            Log.e(TAG, exception.stackTraceToString())
            emit(ResourceState.Error(message = "Cannot create a new form"))
        }
    }

    /**
     * Get a form of a project by formId.
     *
     */
    fun getField(fieldId: String, skipCached: Boolean = false): Flow<ResourceState<Field>> {
        if (!skipCached && cachedFields.containsKey(fieldId))
            return flowOf(ResourceState.Success(cachedFields[fieldId]!!))

        return flow<ResourceState<Field>> {
            val response = projectService.getField(fieldId)
            val form = mapResponseToField(response)
            emit(ResourceState.Success(form))
        }.catch { exception ->
            Log.e(TAG, exception.message ?: "Unknown exception")
            Log.e(TAG, exception.stackTraceToString())
            emit(ResourceState.Error(message = "Cannot get a form"))
        }
    }

    /**
     * Create a new field of form.
     */
    fun createField(
        formId: String,
        name: String,
        numberOrder: Int
    ): Flow<ResourceState<String>> {
        val body = CreateFieldRequest(
            fieldName = name,
            numberOrder = numberOrder,
        )

        return flow<ResourceState<String>> {
            val fieldId = projectService.createField(formId, body)
            val newField = Field(
                id = fieldId,
                name = name,
                formId = formId
            )
            cachedFields[fieldId] = newField
            emit(ResourceState.Success(fieldId))
        }.catch { exception ->
            Log.e(TAG, exception.message, exception)
            emit(ResourceState.Error(message = "Cannot create a new field"))
        }
    }

    /**
     * Update a form of project.
     */
//    fun updateForm(
//        formId: String,
//        title: String,
//        description: String? = null
//    ): Flow<ResourceState<Boolean>> {
//        var updateRequest = UpdateFormRequest(
//            title = title,
//            description = description
//        )
//        return flow<ResourceState<Boolean>> {
//            val updateResult =
//                projectService.updateForm(formId = formId, updateRequestData = updateRequest)
//            // get updated form from server
//            if (updateResult) getForm(formId = formId, skipCached = true)
//            emit(ResourceState.Success(true))
//        }.catch { exception ->
//            Log.e(TAG, exception.message, exception)
//            emit(ResourceState.Error(message = "Cannot update form"))
//        }
//    }


    private fun mapResponseToField(response: FieldResponse): Field {
        return Field(
            id = response.id,
            name = response.name ?: " ",
            formId = response.formId
        )
    }

}