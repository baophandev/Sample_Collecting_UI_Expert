package com.application.data.entity.response

data class ProjectResponse (
    val id: String,
    val thumbnailId: String? = null,
    val name: String,
    val description: String? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val ownerId: String,
    val memberIds: List<String>? = null
)