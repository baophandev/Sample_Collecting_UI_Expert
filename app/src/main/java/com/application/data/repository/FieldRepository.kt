package com.application.data.repository

import android.util.Log
import com.application.R
import com.application.data.datasource.IProjectService
import com.application.data.entity.Field
import com.application.data.entity.request.CreateFieldRequest
import com.application.data.entity.request.UpdateFieldRequest
import com.application.data.entity.response.FieldResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import com.application.data.repository.ProjectRepository.Companion.TAG
import com.sc.library.utility.state.ResourceState
import kotlinx.coroutines.flow.flowOf

class FieldRepository(
    private val projectService: IProjectService,
) {
    private val cachedFields: MutableMap<String, Field> = mutableMapOf()

    //Field
    /**
     * Get all field of a form by formId.
     *
     */
    fun getAllFields(
        formId: String,
        skipCached: Boolean = false
    ): Flow<ResourceState<List<Field>>> {
        if (!skipCached) {
            val fields = cachedFields.filter { it.value.formId == formId }.values.toList()
            return flowOf(ResourceState.Success(fields))
        }

        return flow<ResourceState<List<Field>>> {
            val fields = projectService.getAllFields(formId)
                .sortedBy { it.numberOrder }
                .map(::mapResponseToField)
            cachedFields.putAll(fields.map { Pair(it.id, it) })
            emit(ResourceState.Success(fields))
        }.catch { exception ->
            Log.e(TAG, exception.message, exception)
            emit(
                ResourceState.Error(
                    message = "Cannot get all fields of form.",
                    resId = R.string.get_fields_error
                )
            )
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
            val field = mapResponseToField(response)
            cachedFields[fieldId] = field
            emit(ResourceState.Success(field))
        }.catch { exception ->
            Log.e(TAG, exception.message, exception)
            emit(
                ResourceState.Error(
                    message = "Cannot get a field",
                    resId = R.string.get_field_error
                )
            )
        }
    }

    /**
     * Create a new field of form.
     */
    fun createField(
        formId: String,
        name: String,
        numberOrder: Int
    ): Flow<ResourceState<String>> = flow<ResourceState<String>> {
        val body = CreateFieldRequest(
            fieldName = name,
            numberOrder = numberOrder,
        )
        val fieldId = projectService.createField(formId, body)
        val newField = Field(
            id = fieldId,
            numberOrder = numberOrder,
            name = name,
            formId = formId
        )
        cachedFields[fieldId] = newField
        emit(ResourceState.Success(fieldId))
    }.catch { exception ->
        Log.e(TAG, exception.message, exception)
        emit(
            ResourceState.Error(
                message = "Cannot create a new field",
                resId = R.string.create_field_error
            )
        )
    }

    fun updateField(
        fieldId: String,
        fieldName: String? = null,
        numberOrder: Int? = null
    ): Flow<ResourceState<Boolean>> = flow<ResourceState<Boolean>> {
        val updateRequest = UpdateFieldRequest(
            fieldName = fieldName,
            numberOrder = numberOrder
        )
        val updateResult =
            projectService.updateField(fieldId = fieldId, updateRequestData = updateRequest)
        emit(ResourceState.Success(updateResult))
    }.catch { exception ->
        Log.e(TAG, exception.message, exception)
        emit(
            ResourceState.Error(
                message = "Cannot update field",
                resId = R.string.update_field_error
            )
        )
    }

    fun deleteField(fieldId: String): Flow<ResourceState<Boolean>> = flow<ResourceState<Boolean>> {
        val updateResult = projectService.deleteField(fieldId = fieldId)
        if (updateResult) cachedFields.remove(fieldId)
        emit(ResourceState.Success(updateResult))
    }.catch { exception ->
        Log.e(TAG, exception.message, exception)
        emit(
            ResourceState.Error(
                message = "Cannot delete field",
                resId = R.string.delete_field_error
            )
        )
    }

    private fun mapResponseToField(response: FieldResponse): Field {
        return Field(
            id = response.id,
            numberOrder = response.numberOrder,
            name = response.name,
            formId = response.formId
        )
    }

}