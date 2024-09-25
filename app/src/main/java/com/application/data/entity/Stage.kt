package com.application.data.entity

/**
 * @param emailMembers Map.Entry(generatedId, email)
 */
data class Stage(
    val id: String,
    var title: String? = null,
    var description: String? = null,
    var startDate: String? = null,
    var endDate: String? = null,
    var emailMembers: Map<String, String>? = null,
    var formId: String? = null,
    var projectId: String,
)