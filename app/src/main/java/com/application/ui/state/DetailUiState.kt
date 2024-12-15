package com.application.ui.state

import com.application.constant.UiStatus
import com.application.data.entity.Project
import com.sc.library.user.entity.User

data class DetailUiState(
    val status: UiStatus = UiStatus.INIT,
    val error: Int? = null,
    val loggedUser: User? = null,
    val project: Project? = null,
)