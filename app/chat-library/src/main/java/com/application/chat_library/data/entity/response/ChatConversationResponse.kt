package com.application.chat_library.data.entity.response

data class ChatConversationResponse(
    val id: Long,
    val title: String,
    val messageCount: Long,
    val participantCount: Int,
    val createdAt: Long,
    val updatedAt: Long,
    val creatorId: String
)
