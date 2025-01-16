package com.application.data.entity.response

import com.google.gson.annotations.SerializedName

data class PostResponse(
    val postId: String,
    val status: Boolean,
    @SerializedName("created_at")
    val createdAt: String,
    val title: String,
    val ownerId: String,
    val expertId: String,
    val conversationId: Long,
    @SerializedName("domains")
    val domainIds: List<String>,
    val fileIds: List<String>,
    val generalComment: GeneralCommentResponse? = null
)