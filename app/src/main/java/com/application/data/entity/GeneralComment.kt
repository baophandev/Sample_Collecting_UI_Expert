package com.application.data.entity

import com.sc.library.attachment.entity.Attachment

data class GeneralComment(
    val content: String,
    val attachments: List<Attachment>? = null
)