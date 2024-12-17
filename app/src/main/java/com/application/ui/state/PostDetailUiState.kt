package com.application.ui.state

import com.application.constant.UiStatus
import com.application.data.entity.Comment
import com.application.data.entity.FileInPost
import com.application.data.entity.GeneralComment
import com.application.data.entity.Post

/**
 * @param newComments (fileId, Comment)
 */
data class PostDetailUiState(
    val status: UiStatus = UiStatus.INIT,
    val post: Post? = null,
    val files: List<FileInPost> = emptyList(),
    val newGeneralComment: GeneralComment? = null,
    val newComments: Map<String, Comment> = emptyMap(),
    val error: Int? = null
)
