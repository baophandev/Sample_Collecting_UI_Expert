package com.application.ui.state

import androidx.annotation.StringRes
import com.application.constant.UiStatus
import com.application.data.entity.Project
import com.sc.library.user.entity.User

data class ModifyProjectUiState(
    val status: UiStatus = UiStatus.INIT,
    @StringRes val error: Int? = null,
    val project: Project? = null,
    val projectUsers: List<User> = emptyList(),
    val projectUsersMap: Map<String, String> = emptyMap(), // key: email -> value: memberId
    val updatedMemberIds: List<String> = emptyList(),
    val deletedMemberIds: List<String> = emptyList(),
    val isUpdated: Boolean = false,
    val isThumbnailUpdated: Boolean = false
)