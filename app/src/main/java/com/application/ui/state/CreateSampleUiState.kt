package com.application.ui.state

import androidx.annotation.StringRes
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

data class CreateSampleUiState(
    val blockFields: SnapshotStateList<Pair<String, String>> = mutableStateListOf(),
    val flexFields: SnapshotStateList<Pair<String, String>> = mutableStateListOf(),
    val loading: Boolean = false,
    @StringRes val error: Int? = null,
)
