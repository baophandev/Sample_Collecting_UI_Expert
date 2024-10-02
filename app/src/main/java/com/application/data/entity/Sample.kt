package com.application.data.entity

/**
 * @param data Map.Entry(fieldName, fieldValue)
 */
data class Sample(
    val id: String,
    var writtenBy: String? = null,
    var data: Map<String, String>? = null,
    var projectId: String,
)