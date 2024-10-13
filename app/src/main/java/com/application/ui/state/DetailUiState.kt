package com.application.ui.state

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.application.constant.UiStatus
import com.application.data.entity.Form
import com.application.data.entity.Project
import com.application.data.entity.Stage

/**
 * @param stages Pair(stageId, Pair(stageTitle, stageDescription))
 */
data class DetailUiState(
    val status: UiStatus = UiStatus.INIT,
    val error: String? = null,
    val project: Project? = null,
    val stages: List<Stage> = listOf(),
    val forms: List<Form> = listOf()
)