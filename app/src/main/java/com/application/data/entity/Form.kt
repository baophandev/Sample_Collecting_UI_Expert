package com.application.data.entity

/**
 * @param fields Map.Entry(fieldId, fieldName)
 */
data class Form(
    val id: String,
    var name: String? = null,
    var fields: Map<String, String>? = null
)
