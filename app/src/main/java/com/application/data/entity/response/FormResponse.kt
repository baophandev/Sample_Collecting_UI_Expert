package com.application.data.entity.response

data class FormResponse(
    val id: String,
    val title: String,
    val description: String? = null,
    val projectOwnerId:String,
    val usageStageIds: List<String>? = null
)