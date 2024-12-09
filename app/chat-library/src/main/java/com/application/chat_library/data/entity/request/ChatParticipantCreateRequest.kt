package com.application.chat_library.data.entity.request

import com.application.constant.ChatParticipantType

data class ChatParticipantCreateRequest(
    val userId: String,
    val type: ChatParticipantType,
    val conversationId: Long
)
