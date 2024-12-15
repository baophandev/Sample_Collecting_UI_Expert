package com.application.data.entity.request

data class UpdateMemberRequest(
    val memberId: String,
    val operator: String = "ADD"
)