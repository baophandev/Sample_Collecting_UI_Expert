package com.application.ui.state

import androidx.annotation.StringRes
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

data class FormUiState (
    val title: String = "",
    val fields: SnapshotStateList<String> = mutableStateListOf(),
    val loading: Boolean = false,
    @StringRes val error: Int? = null
)