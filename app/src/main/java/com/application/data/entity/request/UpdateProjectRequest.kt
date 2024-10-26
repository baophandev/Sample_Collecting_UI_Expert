package com.application.data.entity.request

import com.application.constant.ProjectStatus

data class UpdateProjectRequest(
    val thumbnailId: String? = null,
    val name: String? = null,
    val description: String? = null,
    val status: ProjectStatus? = null,
    val startDate: String? = null,
    val endDate: String? = null,
)