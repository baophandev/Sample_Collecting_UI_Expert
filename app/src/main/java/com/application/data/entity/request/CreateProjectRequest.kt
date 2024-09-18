package com.application.data.entity.request

import java.util.Date

data class CreateProjectRequest(
    val name: String,
    val description: String?,
    val startDate: String?,
    val endDate: String?,
    val ownerId: String,
    val memberIds: List<String>?
)
