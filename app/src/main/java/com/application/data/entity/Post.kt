package com.application.data.entity

import android.net.Uri
import com.sc.library.user.entity.User

data class Post(
    val id: String,
    val thumbnail: Uri? = null,
    val createdAt: String,
    val title: String,
    val owner: User,
    val expert: User? = null,
    val generalComment: GeneralComment? = null
)