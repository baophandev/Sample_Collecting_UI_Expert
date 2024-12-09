package com.application.chat_library.data.entity.response

import com.application.chat_library.constant.ChatConversationType

data class ChatConversationCreateRequest(
    val title: String,
    val type: ChatConversationType,
    val creatorId: String
)
