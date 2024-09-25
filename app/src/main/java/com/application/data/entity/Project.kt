package com.application.data.entity

import android.net.Uri

data class Project(
    val id: String,
    var thumbnail: Uri? = null,
    var name: String? = null,
    var description: String? = null,
    var startDate: String? = null,
    var endDate: String? = null,
    val owner: User,
    var memberUsernames: List<String>? = null,
)