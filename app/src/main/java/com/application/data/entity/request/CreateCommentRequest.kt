package com.application.data.entity.request

data class CreateCommentRequest(
    val content: String,
    val attachmentIds: List<String>? = null
)
