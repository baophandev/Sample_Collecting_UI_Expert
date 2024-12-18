package com.application.data.datasource.impl

import com.application.constant.ProjectQueryType
import com.application.constant.ProjectStatus
import com.application.data.datasource.IProjectService
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
import com.sc.library.utility.client.AbstractClient
import com.sc.library.utility.client.response.PagingResponse
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.parameters

class ProjectServiceImpl(
    baseUrl: String,
    timeout: Long = 5000,
) : IProjectService, AbstractClient() {
    private val client = getClient(baseUrl = baseUrl, timeout = timeout)

    override suspend fun checkMemberInAnyStage(projectId: String, userId: String): Boolean {
        val response = client.patch(urlString = "project/$projectId/stage/$userId")
        return response.status == HttpStatusCode.NoContent
    }

    //Project
    override suspend fun createProject(body: CreateProjectRequest): String {
        return client.post("project") {
            setBody(body)
        }.body()
    }

    override suspend fun getAllProjects(
        userId: String,
        query: ProjectQueryType,
        status: ProjectStatus,
        pageNumber: Int,
        pageSize: Int
    ): PagingResponse<ProjectResponse> = runCatching<PagingResponse<ProjectResponse>> {
        client.get(urlString = "project/$userId/user") {
            url {
                encodedParameters.append("query", query.name)
                encodedParameters.append("status", status.name)
                encodedParameters.append("pageNumber", "$pageNumber")
                encodedParameters.append("pageSize", "$pageSize")
            }
        }.body()
    }.getOrDefault(PagingResponse())

    override suspend fun getProject(projectId: String): ProjectResponse {
        return client.get(urlString = "project/$projectId")
            .body()
    }

    override suspend fun updateProject(
        projectId: String,
        updateRequestData: UpdateProjectRequest
    ): Boolean {
        val response = client.patch(urlString = "project/$projectId") {
            contentType(ContentType.Application.Json)
            setBody(updateRequestData)
        }
        return response.status == HttpStatusCode.NoContent
    }

    override suspend fun updateProjectMember(
        projectId: String,
        updateMemberRequest: UpdateMemberRequest
    ): Boolean {
        val response = client.patch(urlString = "project/$projectId/member") {
            contentType(ContentType.Application.Json)
            setBody(updateMemberRequest)
        }
        return response.status == HttpStatusCode.NoContent
    }

    override suspend fun deleteProject(projectId: String): Boolean {
        val response = client.delete(urlString = "project/$projectId")
        return response.status == HttpStatusCode.NoContent
    }


    //Stage
    override suspend fun createStage(body: CreateStageRequest): String {
        return client.post("stage") {
            setBody(body)
        }.body()
    }

    override suspend fun getAllStages(
        projectId: String,
        pageNumber: Int,
        pageSize: Int
    ): PagingResponse<StageResponse> = runCatching<PagingResponse<StageResponse>> {
        client.get(urlString = "stage/$projectId/project") {
            url {
                parameters {
                    append("pageNumber", "$pageNumber")
                    append("pageSize", "$pageSize")
                }
            }
        }.body()
    }.getOrDefault(PagingResponse())

    override suspend fun getStage(stageId: String): StageResponse {
        return client.get(urlString = "stage/$stageId").body()
    }

    override suspend fun updateStage(
        stageId: String,
        updateRequestData: UpdateStageRequest
    ): Boolean {
        val response = client.patch(urlString = "stage/$stageId") {
            contentType(ContentType.Application.Json)
            setBody(updateRequestData)
        }
        return response.status == HttpStatusCode.NoContent
    }

    override suspend fun updateStageMember(
        stageId: String,
        updateMemberRequest: UpdateMemberRequest
    ): Boolean {
        val response = client.patch(urlString = "stage/$stageId/member") {
            contentType(ContentType.Application.Json)
            setBody(updateMemberRequest)
        }
        return response.status == HttpStatusCode.NoContent
    }

    override suspend fun deleteStage(stageId: String): Boolean {
        val response = client.delete(urlString = "stage/$stageId")
        return response.status == HttpStatusCode.NoContent
    }


    //Form
    override suspend fun createForm(body: CreateFormRequest): String {
        return client.post("form") {
            setBody(body)
        }.body()
    }

    override suspend fun getAllForms(
        projectId: String,
        pageNumber: Int,
        pageSize: Int
    ): PagingResponse<FormResponse> = runCatching<PagingResponse<FormResponse>> {
        client.get(urlString = "form/$projectId/project") {
            url {
                parameters {
                    append("pageNumber", "$pageNumber")
                    append("pageSize", "$pageSize")
                }
            }
        }.body()
    }.getOrDefault(PagingResponse())

    override suspend fun getForm(formId: String): FormResponse {
        return client.get(urlString = "form/$formId").body()
    }

    override suspend fun updateForm(formId: String, updateRequestData: UpdateFormRequest): Boolean {
        val response = client.patch(urlString = "form/$formId") {
            contentType(ContentType.Application.Json)
            setBody(updateRequestData)
        }
        return response.status == HttpStatusCode.NoContent
    }

    override suspend fun deleteForm(formId: String): Boolean {
        val response = client.delete(urlString = "form/$formId")
        return response.status == HttpStatusCode.NoContent
    }


    //Field
    override suspend fun createField(formId: String, body: CreateFieldRequest): String =
        client.post("field/$formId") {
            setBody(body)
        }.body()

    override suspend fun createDynamicField(sampleId: String, body: CreateFieldRequest): String =
        client.post("field/$sampleId/dynamic") {
            setBody(body)
        }.body()

    override suspend fun getAllFields(formId: String): List<FieldResponse> =
        client.get(urlString = "field/$formId/form")
            .body()

    override suspend fun getField(formId: String): FieldResponse {
        return client.get(urlString = "field/$formId").body()
    }

    override suspend fun updateField(
        fieldId: String,
        updateRequestData: UpdateFieldRequest
    ): Boolean = client
        .patch(urlString = "field/$fieldId") {
            contentType(ContentType.Application.Json)
            setBody(updateRequestData)
        }
        .status == HttpStatusCode.NoContent

    override suspend fun deleteField(fieldId: String): Boolean = client
        .delete(urlString = "field/$fieldId")
        .status == HttpStatusCode.NoContent

    override suspend fun deleteDynamicField(fieldId: String): Boolean = client
        .delete(urlString = "field/$fieldId/dynamic")
        .status == HttpStatusCode.NoContent

    override suspend fun updateDynamicField(
        fieldId: String,
        body: UpdateDynamicFieldRequest
    ): Boolean = client
        .patch("field/$fieldId/dynamic") {
            setBody(body)
        }.body()

    //Sample
    override suspend fun createSample(body: CreateSampleRequest): String = client
        .post("sample") {
            setBody(body)
        }.body()

    override suspend fun getSample(sampleId: String): SampleResponse = client
        .get(urlString = "sample/$sampleId")
        .body()

    override suspend fun deleteSample(sampleId: String): Boolean = client
        .delete(urlString = "sample/$sampleId")
        .status == HttpStatusCode.NoContent

    override suspend fun getAllSamplesOfStage(
        stageId: String,
        pageNumber: Int,
        pageSize: Int
    ): PagingResponse<SampleResponse> = runCatching<PagingResponse<SampleResponse>> {
        client
            .get(urlString = "sample/$stageId/stage") {
                url {
                    parameters {
                        append("pageNumber", "$pageNumber")
                        append("pageSize", "$pageSize")
                    }
                }
            }
            .body()
    }.getOrDefault(PagingResponse())

    override suspend fun getAllSamplesOfProject(
        projectId: String,
        pageNumber: Int,
        pageSize: Int
    ): PagingResponse<SampleResponse> = runCatching<PagingResponse<SampleResponse>> {
        client
            .get(urlString = "sample/$projectId/project") {
                url {
                    parameters {
                        append("pageNumber", "$pageNumber")
                        append("pageSize", "$pageSize")
                    }
                }
            }
            .body()
    }.getOrDefault(PagingResponse())
}