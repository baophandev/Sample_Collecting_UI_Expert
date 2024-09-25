package com.application.data.datasource.impl

import com.application.constant.ProjectQueryType
import com.application.constant.ProjectStatus
import com.application.constant.ServiceHost
import com.application.data.datasource.IProjectService
import com.application.data.entity.request.CreateProjectRequest
import com.application.data.entity.response.ProjectResponse
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.parameters

class ProjectServiceImpl : IProjectService, AbstractClient() {

    private val client = getClient("http://${ServiceHost.GATEWAY_SERVER}/api/v1/")

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
}