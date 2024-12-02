package com.application.chat_library.data.entity.response

import com.application.constant.ChatMessageType

data class ChatMessageResponse(
    val id: Long,
    val type: ChatMessageType,
    val text: String,
    val attachmentIds: List<String>,
    val createdAt: Long,
    val senderId: String,
    val conversationId: Long
)
