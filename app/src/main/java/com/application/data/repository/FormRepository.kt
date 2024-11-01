package com.application.data.repository

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.application.data.datasource.IProjectService
import com.application.data.entity.Form
import com.application.data.entity.request.CreateFormRequest
import com.application.data.entity.response.FormResponse
import com.application.data.repository.ProjectRepository.Companion.TAG
import com.application.util.ResourceState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class FormRepository(
    private val projectService: IProjectService,
) : PagingSource<Int, Form>() {
    private val cachedForms: MutableMap<String, Form> = mutableMapOf()

    override fun getRefreshKey(state: PagingState<Int, Form>): Int? {
        TODO("Not yet implemented")
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Form> {
        TODO("Not yet implemented")
    }

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
            Log.e(TAG, exception.message ?: "Unknown exception")
            Log.e(TAG, exception.stackTraceToString())
            emit(ResourceState.Error(message = "Cannot create a new form"))
        }
    }

    /**
     * Get a form of a project by formId.
     *
     */
    fun getForm(formId: String): Flow<ResourceState<Form>> {
        if (cachedForms.containsKey(formId))
            return flowOf(ResourceState.Success(cachedForms[formId]!!))

        return flow<ResourceState<Form>> {
            val response = projectService.getForm(formId)
            val form = mapResponseToForm(response)
            emit(ResourceState.Success(form))
        }.catch { exception ->
            Log.e(TAG, exception.message ?: "Unknown exception")
            Log.e(TAG, exception.stackTraceToString())
            emit(ResourceState.Error(message = "Cannot get a form"))
        }
    }

    /**
     * Create a new form of project.
     */
    fun createForm(
        title: String,
        description: String? = null,
        projectOwnerId: String
    ): Flow<ResourceState<String>> {
        val body = CreateFormRequest(
            title = title,
            description = description,
            projectOwnerId = projectOwnerId
        )

        return flow<ResourceState<String>> {
            val formId = projectService.createForm(body)
            val newForm = Form(
                id = formId,
                title = title,
                description = description,
                projectOwnerId = projectOwnerId,
            )
            cachedForms[formId] = newForm
            emit(ResourceState.Success(formId))
        }.catch { exception ->
            Log.e(TAG, exception.message ?: "Unknown exception")
            Log.e(TAG, exception.stackTraceToString())
            emit(ResourceState.Error(message = "Cannot create a new form"))
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