package com.application.data.entity.response

data class StageResponse(
    val id: String,
    val name: String,
    val description: String? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val formId: String,
    val projectOwnerId: String,
    val memberIds: List<String>? = null
)