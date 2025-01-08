package com.application.ui.state

import com.application.constant.UiStatus
import com.application.data.entity.Project

data class DetailUiState(
    val status: UiStatus = UiStatus.INIT,
    val error: Int? = null,
    val project: Project? = null,
)