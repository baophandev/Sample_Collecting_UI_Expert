package com.application.ui.state

import com.application.constant.UiStatus
import com.application.data.entity.Form
import com.application.data.entity.Project
import com.application.data.entity.Stage

data class DetailUiState(
    val status: UiStatus = UiStatus.INIT,
    val error: Int? = null,
    val project: Project? = null,
    val stages: List<Stage> = listOf(),
    val stageStatus: UiStatus = UiStatus.INIT,
    val stageError: Int? = null,
    val forms: List<Form> = listOf()
)