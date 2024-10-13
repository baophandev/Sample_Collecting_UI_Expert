package com.application.ui.state

import android.net.Uri
import androidx.annotation.StringRes
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.application.constant.UiStatus
import com.application.data.entity.Sample
import com.application.data.entity.Stage

data class StageDetailUiState(
    val status: UiStatus = UiStatus.INIT,
    val error: String? = null,
    val imageUris: SnapshotStateList<Pair<String, Uri>> = mutableStateListOf(), // nho sua cho nay nhe ban TODO()
    val sample: Sample? = null, // moi lan hien 1 sample
    val stage: Stage?= null,
)
