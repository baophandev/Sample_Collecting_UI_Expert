package com.application.data.entity.request

import java.time.LocalDate

data class CreateProjectRequest(
    val name: String,
    val description: String?,
    val startDate: LocalDate?,
    val endDate: LocalDate?,
    val ownerId: String,
    val memberIds: List<String>?
)
