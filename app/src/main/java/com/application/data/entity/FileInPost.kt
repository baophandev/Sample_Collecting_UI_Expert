package com.application.data.entity

import android.net.Uri

data class FileInPost(
    val id: String,
    val image: Uri,
    val description: String? = null,
    val comment: Comment? = null,
    val postId: String
)