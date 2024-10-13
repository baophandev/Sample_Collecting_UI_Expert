package com.application.data.entity

/**
 * @param fields Map.Entry(fieldId, fieldName)
 */
data class Form(
    val id: String,
    var title: String,
    var description: String? = null,
    val projectOwnerId:String
)
