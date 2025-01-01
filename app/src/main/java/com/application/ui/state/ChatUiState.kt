package com.application.ui.state

import com.application.constant.UiStatus
import com.application.data.entity.Post
import io.github.nhatbangle.sc.chat.data.entity.Conversation

data class ChatUiState(
    val status: UiStatus = UiStatus.INIT,
    val sendingStatus: UiStatus = UiStatus.INIT,
    val post: Post? = null,
    val conversation: Conversation? = null,
    val text: String = "",
    val error: Int? = null
)
