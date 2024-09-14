package com.application.ui.state

import android.net.Uri
import androidx.annotation.StringRes
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.application.data.entity.Project

data class HomeUiState(
    val projects: SnapshotStateList<Pair<Uri?, Project>> = mutableStateListOf(),
    val init: Boolean = false,
    val loading: Boolean = false,
    @StringRes val error: Int? = null,
    @StringRes val cancel: Int? = null
)