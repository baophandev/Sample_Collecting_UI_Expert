package com.application.data.entity.response

data class AttachmentResponse(
    val fileId: String,
    val fileName: String,
    val fileType: String,
    val downloadPath: String
)
