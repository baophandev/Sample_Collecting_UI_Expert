package com.application.data.entity.response

import com.google.gson.annotations.SerializedName

data class FileInPostResponse(
    val fileId: String,
    val description: String,
    @SerializedName("commentResponseDto")
    val comment: CommentResponse
)