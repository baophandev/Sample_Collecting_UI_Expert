package com.application.data.datasource

import com.application.constant.ProjectQueryType
import com.application.constant.ProjectStatus
import com.application.data.entity.request.CreateFieldRequest
import com.application.data.entity.request.CreateFormRequest
import com.application.data.entity.request.CreateProjectRequest
import com.application.data.entity.request.CreateSampleRequest
import com.application.data.entity.request.CreateStageRequest
import com.application.data.entity.request.UpdateFieldRequest
import com.application.data.entity.request.UpdateFormRequest
import com.application.data.entity.request.UpdateProjectRequest
import com.application.data.entity.request.UpdateStageRequest
import com.application.data.entity.response.FieldResponse
import com.application.data.entity.response.FormResponse
import com.application.data.entity.response.PagingResponse
import com.application.data.entity.response.ProjectResponse
import com.application.data.entity.response.StageResponse

interface IProjectService {

    //Project
    suspend fun createProject(body: CreateProjectRequest): String
    suspend fun getAllProjects(
        userId: String,
        query: ProjectQueryType = ProjectQueryType.ALL,
        status: ProjectStatus = ProjectStatus.NORMAL,
        pageNumber: Int = 0,
        pageSize: Int = 6
    ): PagingResponse<ProjectResponse>
    suspend fun getProject(projectId: String): ProjectResponse
    suspend fun updateProject(
        projectId: String,
        updateRequestData: UpdateProjectRequest
    ): Boolean



    //Stage of Project
    suspend fun createStage(body: CreateStageRequest): String
    suspend fun getAllStages(
        projectId: String,
        pageNumber: Int = 0,
        pageSize: Int = 6
    ): PagingResponse<StageResponse>
    suspend fun getStage(stageId: String): StageResponse
    suspend fun updateStage(
        stageId: String,
        updateRequestData: UpdateStageRequest
    ): Boolean


    //Form of Stage
    suspend fun createForm(body: CreateFormRequest): String
    suspend fun getAllForms(
        projectId: String,
        pageNumber: Int = 0,
        pageSize: Int = 6
    ): PagingResponse<FormResponse>
    suspend fun getForm(formId: String): FormResponse
    suspend fun updateForm(
        formId: String,
        updateRequestData: UpdateFormRequest
    ): Boolean

    //Field of Form
    suspend fun createField(formId: String, body: CreateFieldRequest): String
    suspend fun getAllField(
        formId: String,
        pageNumber: Int = 0,
        pageSize: Int = 6
    ): List<FieldResponse>
    suspend fun getField(formId: String): FieldResponse
    suspend fun updateField(
        formId: String,
        updateRequestData: UpdateFieldRequest
    ): Boolean

    // Sample of Stage
    suspend fun createSample(body: CreateSampleRequest): String
}