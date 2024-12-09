package com.application.data.entity.request

data class UpdateStageRequest(
    val name: String?= null,
    val description: String?= null,
    val startDate: String?= null,
    val endDate: String?= null,
    val formId: String?= null,
)