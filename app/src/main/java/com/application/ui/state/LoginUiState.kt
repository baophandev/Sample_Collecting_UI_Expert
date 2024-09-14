package com.application.ui.state

import androidx.annotation.StringRes

data class LoginUiState(
    val loading: Boolean = false,
    @StringRes val cancel: Int? = null,
    @StringRes val error: Int? = null
)
