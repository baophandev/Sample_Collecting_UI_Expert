package com.application.data.datasource

import com.application.constant.ProjectQueryType
import com.application.constant.ProjectStatus
import com.application.data.entity.request.CreateFieldRequest
import com.application.data.entity.request.CreateFormRequest
import com.application.data.entity.request.CreateProjectRequest
import com.application.data.entity.request.CreateStageRequest
import com.application.data.entity.request.UpdateFormRequest
import com.application.data.entity.request.UpdateProjectRequest
import com.application.data.entity.request.UpdateStageRequest
import com.application.data.entity.response.FieldResponse
import com.application.data.entity.response.FormResponse
import com.application.data.entity.response.ProjectResponse
import com.application.data.entity.response.StageResponse

interface IProjectService {

    //Project
    suspend fun createProject(body: CreateProjectRequest): String
    suspend fun getAllProject(
        userId: String,
        query: ProjectQueryType = ProjectQueryType.ALL,
        status: ProjectStatus = ProjectStatus.NORMAL,
        pageNumber: Int = 0,
        pageSize: Int = 6
    ): List<ProjectResponse>
    suspend fun getProject(projectId: String): ProjectResponse
    suspend fun updateProject(
        projectId: String,
        updateRequestData: UpdateProjectRequest
    ): Boolean



    //Stage of Project
    suspend fun createStage(body: CreateStageRequest): String
    suspend fun getAllStage(
        projectId: String,
        pageNumber: Int = 0,
        pageSize: Int = 6
    ): List<StageResponse>
    suspend fun getStage(stageId: String): StageResponse
    suspend fun updateStage(
        stageId: String,
        updateRequestData: UpdateStageRequest
    ): Boolean


    //Form of Project
    suspend fun createForm(body: CreateFormRequest): String
    suspend fun getAllForm(
        projectId: String,
        pageNumber: Int = 0,
        pageSize: Int = 6
    ): List<FormResponse>
    suspend fun getForm(formId: String): FormResponse
    suspend fun updateForm(
        formId: String,
        updateRequestData: UpdateFormRequest
    ): Boolean

    //Field
    suspend fun createField(body: CreateFieldRequest): String
    suspend fun getAllField(
        formId: String,
    ): List<FieldResponse>
    suspend fun getField(formId: String): FieldResponse
}