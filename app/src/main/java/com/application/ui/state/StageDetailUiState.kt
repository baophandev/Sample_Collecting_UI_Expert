package com.application.ui.state

import android.net.Uri
import androidx.annotation.StringRes
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.application.data.entity.Sample

data class StageDetailUiState(
    val init: Boolean = false,
    val loading: Boolean = false,
    @StringRes val error: Int? = null,
    val imageUris: SnapshotStateList<Pair<String, Uri>> = mutableStateListOf(),
    val sample: Sample? = null // moi lan hien 1 sample
)
