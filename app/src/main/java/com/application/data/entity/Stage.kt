package com.application.data.entity

/**
 * @param emailMembers Map.Entry(generatedId, email)
 */
data class Stage(
    val id: String,
    val name: String? = null,
    var description: String? = null,
    var startDate: String? = null,
    var endDate: String? = null,
    val formId: String? = null,
    val projectOwnerId: String,
)