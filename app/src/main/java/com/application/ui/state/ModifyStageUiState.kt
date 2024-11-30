package com.application.ui.state

import androidx.annotation.StringRes
import com.application.constant.UiStatus
import com.application.data.entity.Stage

data class ModifyStageUiState(
    val status: UiStatus = UiStatus.INIT,
    @StringRes val error: Int? = null,
    val stage: Stage? = null,
    val memberUsernames: List<String> = listOf(),
    val isUpdated: Boolean = false
)
