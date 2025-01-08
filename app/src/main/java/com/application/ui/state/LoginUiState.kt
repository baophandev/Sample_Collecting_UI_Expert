package com.application.ui.state

import androidx.annotation.StringRes
import com.application.constant.UiStatus

data class LoginUiState(
    val status: UiStatus = UiStatus.INIT,
    val username: String = "",
    val password: String = "",
    @StringRes val error: Int? = null,
)

