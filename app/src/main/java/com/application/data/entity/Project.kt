package com.application.data.entity

import android.net.Uri

data class Project(
    var id: String,
    var thumbnailUri: Uri? = null,
    var name: String? = null,
    var description: String? = null,
    var startDate: Long? = null,
    var endDate: Long? = null,
    var owner: User,
    var memberUsernames: List<String>? = null,
    var forms: List<Form>? = null,
    var stages: List<Stage>? = null
)
