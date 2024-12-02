package com.application.chat_library.data.entity.response

import com.application.constant.ChatParticipantType

data class ChatParticipantResponse(
    val id: Long,
    val type: ChatParticipantType,
    val createdAt: Long,
    val conversationId: Long,
    val userId: String
)
