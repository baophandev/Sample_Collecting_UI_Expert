package com.application.data.entity.response

import com.google.gson.annotations.SerializedName

data class CommentResponse(
    val content: String,
    @SerializedName("attachmentFileIds")
    val attachmentIds: List<String>
)