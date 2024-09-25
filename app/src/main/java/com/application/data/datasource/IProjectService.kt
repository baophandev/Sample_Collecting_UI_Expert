package com.application.data.datasource

import com.application.constant.ProjectQueryType
import com.application.constant.ProjectStatus
import com.application.data.entity.request.CreateProjectRequest
import com.application.data.entity.response.ProjectResponse

interface IProjectService {

    suspend fun createProject(body: CreateProjectRequest): String
    suspend fun getAllProject(
        userId: String,
        query: ProjectQueryType = ProjectQueryType.ALL,
        status: ProjectStatus = ProjectStatus.NORMAL,
        pageNumber: Int = 0,
        pageSize: Int = 6
    ): List<ProjectResponse>

}