package com.application.ui.state

import androidx.annotation.StringRes
import com.application.constant.UiStatus
import com.application.data.entity.Project

data class HomeUiState(
    val projects: List<Project> = listOf(),
    val status: UiStatus = UiStatus.INIT,
    @StringRes val error: Int? = null
)