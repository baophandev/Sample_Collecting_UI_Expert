package com.application.data.entity.request

import com.application.constant.MemberOperator

data class UpdateMemberRequest(
    val memberId: String,
    val operator: MemberOperator
)