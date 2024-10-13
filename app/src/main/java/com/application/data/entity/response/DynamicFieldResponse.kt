package com.application.data.entity.response

data class DynamicFieldResponse(
    val id: String,
    val name: String?= null,
    val value: String?= null,
    val numberOrder: Int,
)