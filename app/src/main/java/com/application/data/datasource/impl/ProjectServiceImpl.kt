package com.application.data.datasource.impl

import com.application.constant.ServiceHost
import com.application.data.datasource.AbstractClient
import com.application.data.datasource.IProjectService
import com.application.data.entity.request.CreateProjectRequest
import com.application.data.entity.response.ProjectResponse
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.parameters

class ProjectServiceImpl : IProjectService, AbstractClient() {

    private val client = getClient("http://${ServiceHost.GATEWAY_SERVER}/api/v1/")

    override suspend fun createProject(body: CreateProjectRequest): String {
        return client.post("project") {
            setBody(body)
            contentType(ContentType.Application.Json)
        }.body()
    }

    override suspend fun getAllProject(
        userId: String,
        isOwner: Boolean,
        status: String,
        pageNumber: Int,
        pageSize: Int
    ): List<ProjectResponse>{
        return client.get(urlString = "project/$userId/user"){
            url {
                parameters {
                    append("pageNumber", "$pageNumber")
                    append("pageSize", "$pageSize")
                }
            }
        }.body()
    }
}