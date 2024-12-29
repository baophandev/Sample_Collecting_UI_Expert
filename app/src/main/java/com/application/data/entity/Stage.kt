package com.application.data.entity

import io.github.nhatbangle.sc.user.entity.User

data class Stage(
    val id: String,
    val name: String,
    var description: String? = null,
    var startDate: String? = null,
    var endDate: String? = null,
    val formId: String,
    val projectOwnerId: String,
    val members: List<User>
)