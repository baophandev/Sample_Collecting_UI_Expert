package com.application.ui.state

import androidx.annotation.StringRes
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.application.constant.UiStatus

data class CreateFormUiState(
    val title: String = "",
    val description: String = "",
    val fields: SnapshotStateList<String> = mutableStateListOf(),
    val projectOwnerId: String? = null,
    val status: UiStatus = UiStatus.INIT,
    @StringRes val error: Int? = null
)