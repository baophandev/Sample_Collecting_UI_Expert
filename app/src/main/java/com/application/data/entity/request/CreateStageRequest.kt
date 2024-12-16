package com.application.data.entity.request

data class CreateStageRequest(
    val name: String,
    val description: String?= null,
    val startDate: String?= null,
    val endDate: String?= null,
    val formId: String?= null,
    val projectOwnerId: String,
    val memberIds: List<String>? = null
)