package com.application.data.datasource

import com.application.data.entity.request.CreateProjectRequest

interface IProjectService {

    suspend fun createProject(body: CreateProjectRequest): String

}