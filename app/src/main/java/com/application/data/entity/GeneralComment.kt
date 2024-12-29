package com.application.data.entity

import io.github.nhatbangle.sc.attachment.entity.Attachment

data class GeneralComment(
    val content: String,
    val attachments: List<Attachment>? = null
)