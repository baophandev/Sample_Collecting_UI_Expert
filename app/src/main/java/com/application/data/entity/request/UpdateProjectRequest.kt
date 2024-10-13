package com.application.data.entity.request

data class UpdateProjectRequest(
    val name: String?= null,
    val description: String?= null,
    val status: String?= null,
    val startDate: String?= null,
    val endDate: String?= null,
)