package com.application.chat_library.data.entity.response

data class ChatWebSocketResponse(
    val serverHost: String,
    val serverPort: Int,
    val publishDest: String,
    val subscribeConversationDest: String,
    val subscribeNotificationDest: String
)
