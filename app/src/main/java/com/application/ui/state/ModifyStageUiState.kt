package com.application.ui.state

import androidx.annotation.StringRes
import com.application.constant.UiStatus
import com.application.data.entity.Form
import com.application.data.entity.Stage
import com.sc.library.user.entity.User

data class ModifyStageUiState(
    val status: UiStatus = UiStatus.INIT,
    @StringRes val error: Int? = null,
    val stage: Stage? = null,
    val selectedForm: Form? = null,
    val forms: List<Form> = emptyList(),
    val projectMembers: List<User> = emptyList(),
    val stageUsers: List<User> = emptyList(),
    val addedMemberIds: List<String> = emptyList(),
    val deletedMemberIds: List<String> = emptyList(),
    val isUpdated: Boolean = false
)
