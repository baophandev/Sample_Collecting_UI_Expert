package com.application.android.user_library.entity.response

data class LoginResponse (
    val scope: String,
    val access_token: String,
    val expires_in: Int,
    val refresh_expires_in: Int,
    val token_type: String,
    val not_before_policy: Int,
    val session_state: String
)