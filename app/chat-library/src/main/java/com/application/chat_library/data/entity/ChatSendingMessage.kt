package com.application.chat_library.data.entity

data class ChatSendingMessage(
    val text: String,
    val attachments: List<String>? = null,
    val senderId: String
)
