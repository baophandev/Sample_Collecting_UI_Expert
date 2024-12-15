package com.application.data.entity

import android.net.Uri
import com.sc.library.user.entity.User

data class Project(
    val id: String,
    val thumbnail: Uri? = null,
    val name: String,
    val description: String? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val owner: User,
    val members: List<User>,
)