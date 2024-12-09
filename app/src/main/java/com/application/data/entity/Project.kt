package com.application.data.entity

import android.net.Uri
import com.application.android.user_library.entity.User

data class Project(
    val id: String,
    val thumbnail: Uri? = null,
    val name: String,
    val description: String? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val owner: User,
    val memberUsernames: List<String>? = null,
)