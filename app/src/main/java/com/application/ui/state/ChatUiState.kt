package com.application.ui.state

import android.net.Uri
import com.application.constant.UiStatus
import com.sc.library.chat.data.entity.Conversation

data class ChatUiState(
    val status: UiStatus = UiStatus.INIT,
    val sendingStatus: UiStatus = UiStatus.INIT,
    val conversation: Conversation? = null,
    val text: String = "",
    val selectedAttachments: List<Uri> = emptyList(),
    val error: Int? = null
)
