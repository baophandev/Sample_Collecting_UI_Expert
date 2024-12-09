package com.application.data.entity

data class Form(
    val id: String,
    val title: String,
    val description: String? = null,
    val projectOwnerId:String,
)
