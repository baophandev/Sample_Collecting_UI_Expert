package com.application.data.entity.request

data class CreateFormRequest(
    val title: String,
    val description: String?= null,
    val projectOwnerId: String,
)