package com.application.ui.state

import com.application.constant.UiStatus
import com.application.data.entity.FileInPost
import com.application.data.entity.Post

data class PostDetailUiState(
    val status: UiStatus = UiStatus.INIT,
    val files: List<FileInPost> = emptyList(),
    val post: Post? = null,
    val error: Int? = null
)
