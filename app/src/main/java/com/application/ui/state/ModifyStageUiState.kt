package com.application.ui.state

import androidx.annotation.StringRes
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

data class ModifyStageUiState(
    val init: Boolean = false,
    val loading: Boolean = false,
    @StringRes val error: Int? = null,

    val title: String = "",
    val description: String = "",
    val startDate: Long? = null,
    val endDate: Long? = null,
    val emailMembers: SnapshotStateList<String> = mutableStateListOf(),
    val formId: String = "",
)
