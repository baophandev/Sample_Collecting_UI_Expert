package com.application.chat_library.data.entity

import com.application.constant.ChatMessageType
import java.sql.Timestamp

data class ChatReceivingMessage(
    val id: Long,
    val type: ChatMessageType,
    val text: String,
    val createdAt: Timestamp,
    val senderId: String
)
