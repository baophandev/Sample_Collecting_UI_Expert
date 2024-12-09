package com.application.android.user_library.entity.request

data class UserRegisterRequest(
    val username: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val gender: String,
    val birthdate: String,
)
