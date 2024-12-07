package com.application.ui.state

import androidx.annotation.StringRes
import com.application.constant.UiStatus
import com.application.data.entity.Form
import com.application.data.entity.Stage

data class ModifyStageUiState(
    val status: UiStatus = UiStatus.INIT,
    @StringRes val error: Int? = null,
    val stage: Stage? = null,
    val selectedForm: Form? = null,
    val forms: List<Form> = emptyList(),
    val memberUsernames: List<String> = emptyList(),
    val isUpdated: Boolean = false
)
