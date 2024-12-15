package com.application.ui.state

import androidx.annotation.StringRes
import com.application.constant.UiStatus
import com.application.data.entity.Form
import com.sc.library.user.entity.User

data class CreateStageUiState(
    val status: UiStatus = UiStatus.INIT,
    @StringRes val error: Int? = null,

    val name: String = "",
    val description: String = "",
    val startDate: String? = null,
    val endDate: String? = null,

    val selectedForm: Form? = null,
    val projectMembers: List<User> = emptyList(),
    val selectedUsers: List<User> = emptyList()
)