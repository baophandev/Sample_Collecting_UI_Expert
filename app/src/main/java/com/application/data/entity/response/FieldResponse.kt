package com.application.data.entity.response

data class FieldResponse(
    val id: String,
    val numberOrder: Int,
    val name: String?= null,
    val formId: String,
)