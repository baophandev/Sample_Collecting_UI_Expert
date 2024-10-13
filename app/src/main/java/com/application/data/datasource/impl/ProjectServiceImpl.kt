package com.application.data.datasource.impl

import com.application.constant.ProjectQueryType
import com.application.constant.ProjectStatus
import com.application.constant.ServiceHost
import com.application.data.datasource.IProjectService
import com.application.data.entity.request.CreateFieldRequest
import com.application.data.entity.request.CreateFormRequest
import com.application.data.entity.request.CreateProjectRequest
import com.application.data.entity.request.CreateStageRequest
import com.application.data.entity.response.FieldResponse
import com.application.data.entity.response.FormResponse
import com.application.data.entity.response.ProjectResponse
import com.application.data.entity.response.StageResponse
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.parameters

class ProjectServiceImpl : IProjectService, AbstractClient() {

    private val client = getClient("http://${ServiceHost.GATEWAY_SERVER}/api/v1/")


    //Project
    override suspend fun createProject(body: CreateProjectRequest): String {
        return client.post("project") {
            setBody(body)
        }.body()
    }

    override suspend fun getAllProject(
        userId: String,
        query: ProjectQueryType,
        status: ProjectStatus,
        pageNumber: Int,
        pageSize: Int
    ): List<ProjectResponse> {
        return client.get(urlString = "project/$userId/user") {
            url {
                parameters {
                    append("query", query.name)
                    append("status", status.name)
                    append("pageNumber", "$pageNumber")
                    append("pageSize", "$pageSize")
                }
            }
        }.body()
    }

    override suspend fun getProject(projectId: String): ProjectResponse {
        return client.get(urlString = "project/$projectId")
            .body()
    }


    //Stage
    override suspend fun createStage(body: CreateStageRequest): String {
        return client.post("stage"){
            setBody(body)
        }.body()
    }

    override suspend fun getAllStage(
        projectId: String,
        pageNumber: Int,
        pageSize: Int
    ): List<StageResponse> {
        return client.get(urlString = "stage/$projectId/project") {
            url{
                parameters{
                    append("pageNumber", "$pageNumber")
                    append("pageSize", "$pageSize")
                }
            }
        }.body()
    }

    override suspend fun getStage(stageId: String): StageResponse {
        return client.get(urlString = "stage/$stageId").body()
    }


    //Form
    override suspend fun createForm(body: CreateFormRequest): String {
        return client.post("form"){
            setBody(body)
        }.body()
    }

    override suspend fun getAllForm(
        projectId: String,
        pageNumber: Int,
        pageSize: Int
    ): List<FormResponse> {
        return client.get(urlString = "form/$projectId/project"){
            url{
                parameters{
                    append("pageNumber", "$pageNumber")
                    append("pageSize", "$pageSize")
                }
            }
        }.body()
    }

    override suspend fun getForm(formId: String): FormResponse {
        return client.get(urlString = "form/$formId").body()
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