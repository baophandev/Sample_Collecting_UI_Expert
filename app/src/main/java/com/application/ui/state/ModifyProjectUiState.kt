package com.application.ui.state

import androidx.annotation.StringRes
import com.application.constant.UiStatus
import com.application.data.entity.Project
import io.github.nhatbangle.sc.user.entity.User

data class ModifyProjectUiState(
    val status: UiStatus = UiStatus.INIT,
    @StringRes val error: Int? = null,
    val project: Project? = null,
    val projectUsers: List<User> = emptyList(),
    val addedMemberIds: List<String> = emptyList(),
    val deletedMemberIds: List<String> = emptyList(),
    val isUpdated: Boolean = false,
    val isThumbnailUpdated: Boolean = false
)