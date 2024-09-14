package com.application.data.entity

data class Project(
    val projectId: String,
    val data: ProjectData,
)

/**
 * @param emailMembers Map.Entry(generatedId, email)
 * @param forms Map.Entry(formId, Form)
 * @param stages Map.Entry(stageId, Stage)
 */
data class ProjectData(
    var thumbnailPath: String? = null,
    var title: String? = null,
    var startDate: Long? = null,
    var endDate: Long? = null,
    var description: String? = null,
    var emailOwner: String? = null,
    var emailMembers: Map<String, String>? = null,
    var forms: Map<String, Form>? = null,
    var stages: Map<String, Stage>? = null
)

/**
 * @param fields Map.Entry(fieldId, fieldName)
 */
data class Form(
    var name: String? = null,
    var fields: Map<String, String>? = null
)

/**
 * @param emailMembers Map.Entry(generatedId, email)
 */
data class Stage(
    var title: String? = null,
    var description: String? = null,
    var startDate: Long? = null,
    var endDate: Long? = null,
    var emailMembers: Map<String, String>? = null,
    var formId: String? = null
)
