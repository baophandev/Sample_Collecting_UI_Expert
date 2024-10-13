package com.application.data.entity.request

data class UpdateDynamicFieldRequest(
    val name: String?= null,
    val value: String?= null,
    val numberOrder: Int?= null,
)