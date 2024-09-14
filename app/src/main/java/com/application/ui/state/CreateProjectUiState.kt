package com.application.ui.state

import android.net.Uri
import androidx.annotation.StringRes
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import java.util.Date

data class CreateProjectUiState(
    val thumbnail: Pair<String, Uri>? = null,
    val title: String = "",
    val description: String = "",
    val startDate: Date? = null,
    val endDate: Date? = null,
    val emailMembers: SnapshotStateList<String> = mutableStateListOf(),
    val loading: Boolean = false,
    @StringRes val error: Int? = null
)
