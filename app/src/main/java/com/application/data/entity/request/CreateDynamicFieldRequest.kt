package com.application.data.entity.request

data class CreateDynamicFieldRequest(
    val name: String,
    val value: String,
    val numberOrder: Int,
)