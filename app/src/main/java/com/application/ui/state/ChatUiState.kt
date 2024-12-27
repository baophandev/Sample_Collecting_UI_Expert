package com.application.ui.state

import com.application.constant.UiStatus
import com.sc.library.chat.data.entity.Conversation

data class ChatUiState(
    val status: UiStatus = UiStatus.INIT,
    val sendingStatus: UiStatus = UiStatus.INIT,
    val conversation: Conversation? = null,
    val text: String = "",
    val error: Int? = null
)
