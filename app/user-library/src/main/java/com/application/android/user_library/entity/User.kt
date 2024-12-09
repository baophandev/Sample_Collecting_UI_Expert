package com.application.android.user_library.entity

data class User(
    val id: String,
    val firstName: String,
    val lastName: String,
    val email: String
)

data class LoginCertificate(
    val accessToken: String,
    val expiresIn: Int,
    val refreshExpiresIn: Int,
    val tokenType: String,
)

