package com.application.data.entity

/**
 * @param emailMembers Map.Entry(generatedId, email)
 */
data class Stage(
    val id: String,
    var title: String? = null,
    var description: String? = null,
    var startDate: Long? = null,
    var endDate: Long? = null,
    var emailMembers: Map<String, String>? = null,
    var formId: String? = null
)
