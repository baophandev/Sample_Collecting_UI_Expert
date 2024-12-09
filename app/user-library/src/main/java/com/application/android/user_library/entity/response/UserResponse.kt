package com.application.android.user_library.entity.response

import com.application.android.user_library.constant.Gender

data class UserResponse(
    val id: String,
    val firstName: String,
    val lastName: String,
    val gender: Gender,
    val birthDate: String,
    val email: String,
    val domainIds: List<String>
)
