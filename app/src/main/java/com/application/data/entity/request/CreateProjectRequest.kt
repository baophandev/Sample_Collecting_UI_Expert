package com.application.data.entity.request

/**
 * @param startDate Format pattern yyyy-mm-dd
 * @param endDate Format pattern yyyy-mm-dd
 */
data class CreateProjectRequest(
    val thumbnailId: String? = null,
    val name: String,
    val description: String? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val ownerId: String,
    val memberIds: List<String>? = null
)
