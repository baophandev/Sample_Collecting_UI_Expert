package com.application.data.entity.response

import java.time.LocalDate

data class ProjectResponse (
    val id: String,
    val thumbnailId: String,
    val name: String,
    val description: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val ownerId: String,
    val memberIds: List<String>?
)