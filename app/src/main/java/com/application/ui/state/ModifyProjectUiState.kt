package com.application.ui.state

import androidx.annotation.StringRes
import com.application.constant.UiStatus
import com.application.data.entity.Project

data class ModifyProjectUiState(
    val status: UiStatus = UiStatus.INIT,
    @StringRes val error: Int? = null,
    val project: Project? = null,
    val memberUsernames: List<String> = listOf(),
    val isUpdated: Boolean = false
)