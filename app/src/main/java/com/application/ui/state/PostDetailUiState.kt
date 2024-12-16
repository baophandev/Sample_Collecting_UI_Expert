package com.application.ui.state

import com.application.constant.UiStatus
import com.application.data.entity.Post

data class PostDetailUiState(
    val status: UiStatus = UiStatus.INIT,
    val post: Post? = null,
    val error: Int? = null
)
