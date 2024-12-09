package com.application.chat_library.data.entity

import java.sql.Timestamp

data class ChatConversation(
    val id: Long,
    val title: String,
    val creatorId: String,
    val updatedAt: Timestamp
)
