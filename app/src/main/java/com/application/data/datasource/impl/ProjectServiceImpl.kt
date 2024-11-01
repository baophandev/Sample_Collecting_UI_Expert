package com.application.data.datasource.impl

import com.application.constant.ProjectQueryType
import com.application.constant.ProjectStatus
import com.application.constant.ServiceHost
import com.application.data.datasource.IProjectService
import com.application.data.entity.request.CreateFieldRequest
import com.application.data.entity.request.CreateFormRequest
import com.application.data.entity.request.CreateProjectRequest
import com.application.data.entity.request.CreateStageRequest
import com.application.data.entity.request.UpdateFormRequest
import com.application.data.entity.request.UpdateProjectRequest
import com.application.data.entity.request.UpdateStageRequest
import com.application.data.entity.response.FieldResponse
import com.application.data.entity.response.FormResponse
import com.application.data.entity.response.PagingResponse
import com.application.data.entity.response.ProjectResponse
import com.application.data.entity.response.StageResponse
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.parameters

class ProjectServiceImpl : IProjectService, AbstractClient() {
    private val client = getClient("http://${ServiceHost.GATEWAY_SERVER}/api/v1/")

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
    ): PagingResponse<ProjectResponse> {
        return client.get(urlString = "project/$userId/user") {
            url {
                encodedParameters.append("query", query.name)
                encodedParameters.append("status", status.name)
                encodedParameters.append("pageNumber", "$pageNumber")
                encodedParameters.append("pageSize", "$pageSize")
            }
        }.body()
    }

    override suspend fun getProject(projectId: String): ProjectResponse {
        return client.get(urlString = "project/$projectId")
            .body()
    }

    override suspend fun updateProject(
        projectId: String,
        updateRequestData: UpdateProjectRequest
    ): Boolean {
        val response = client.patch(urlString = "project/$projectId"){
            contentType(ContentType.Application.Json)
            setBody(updateRequestData)
        }
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
    ): PagingResponse<StageResponse> {
        return client.get(urlString = "stage/$projectId/project") {
            url {
                parameters {
                    append("pageNumber", "$pageNumber")
                    append("pageSize", "$pageSize")
                }
            }
        }.body()
    }

    override suspend fun getStage(stageId: String): StageResponse {
        return client.get(urlString = "stage/$stageId").body()
    }

    override suspend fun updateStage(
        stageId: String,
        updateRequestData: UpdateStageRequest
    ): Boolean {
        val response = client.patch(urlString = "stage/$stageId"){
            contentType(ContentType.Application.Json)
            setBody(updateRequestData)
        }
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
    ): PagingResponse<FormResponse> {
        return client.get(urlString = "form/$projectId/project") {
            url {
                parameters {
                    append("pageNumber", "$pageNumber")
                    append("pageSize", "$pageSize")
                }
            }
        }.body()
    }

    override suspend fun getForm(formId: String): FormResponse {
        return client.get(urlString = "form/$formId").body()
    }

    override suspend fun updateForm(formId: String, updateRequestData: UpdateFormRequest): Boolean {
        val response = client.patch(urlString = "form/$formId"){
            contentType(ContentType.Application.Json)
            setBody(updateRequestData)
        }
        return response.status == HttpStatusCode.NoContent
    }


    //Field
    override suspend fun createField(body: CreateFieldRequest): String {
        TODO("Not yet implemented")
    }

    override suspend fun getAllField(formId: String): List<FieldResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun getField(formId: String): FieldResponse {
        TODO("Not yet implemented")
    }
}