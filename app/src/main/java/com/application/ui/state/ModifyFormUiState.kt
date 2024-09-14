package com.application.ui.state

import androidx.annotation.StringRes
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

data class ModifyFormUiState(
    val init: Boolean = false,
    val loading: Boolean = false,
    @StringRes val error: Int? = null,
    val name: String = "",
    val fields: SnapshotStateList<String> = mutableStateListOf(),
)
