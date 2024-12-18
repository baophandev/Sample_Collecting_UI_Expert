package com.application.data.datasource

import com.application.constant.ProjectQueryType
import com.application.constant.ProjectStatus
import com.application.data.entity.request.CreateFieldRequest
import com.application.data.entity.request.CreateFormRequest
import com.application.data.entity.request.CreateProjectRequest
import com.application.data.entity.request.CreateSampleRequest
import com.application.data.entity.request.CreateStageRequest
import com.application.data.entity.request.UpdateDynamicFieldRequest
import com.application.data.entity.request.UpdateFieldRequest
import com.application.data.entity.request.UpdateFormRequest
import com.application.data.entity.request.UpdateMemberRequest
import com.application.data.entity.request.UpdateProjectRequest
import com.application.data.entity.request.UpdateStageRequest
import com.application.data.entity.response.FieldResponse
import com.application.data.entity.response.FormResponse
import com.application.data.entity.response.ProjectResponse
import com.application.data.entity.response.SampleResponse
import com.application.data.entity.response.StageResponse
import com.sc.library.utility.client.response.PagingResponse

interface IProjectService {

    //Check member in stage
    suspend fun checkMemberInAnyStage(projectId: String, userId: String): Boolean

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
    suspend fun updateProjectMember(
        projectId: String,
        updateMemberRequest: UpdateMemberRequest
    ): Boolean

    suspend fun deleteProject(projectId: String): Boolean

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
    suspend fun updateStageMember(
        stageId: String,
        updateMemberRequest: UpdateMemberRequest
    ): Boolean

    suspend fun deleteStage(stageId: String): Boolean

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

    suspend fun deleteForm(formId: String): Boolean

    //Field of Form
    suspend fun getAllFields(formId: String): List<FieldResponse>
    suspend fun createField(formId: String, body: CreateFieldRequest): String
    suspend fun deleteField(fieldId: String): Boolean
    suspend fun getField(formId: String): FieldResponse
    suspend fun updateField(
        fieldId: String,
        updateRequestData: UpdateFieldRequest
    ): Boolean

    suspend fun createDynamicField(sampleId: String, body: CreateFieldRequest): String
    suspend fun deleteDynamicField(fieldId: String): Boolean
    suspend fun updateDynamicField(
        fieldId: String,
        body: UpdateDynamicFieldRequest
    ): Boolean

    // Sample of Stage
    suspend fun createSample(body: CreateSampleRequest): String
    suspend fun getSample(sampleId: String): SampleResponse
    suspend fun deleteSample(sampleId: String): Boolean
    suspend fun getAllSamplesOfStage(
        stageId: String,
        pageNumber: Int = 0,
        pageSize: Int = 6
    ): PagingResponse<SampleResponse>

    suspend fun getAllSamplesOfProject(
        projectId: String,
        pageNumber: Int = 0,
        pageSize: Int = 6
    ): PagingResponse<SampleResponse>
}