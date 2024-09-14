package com.application.ui.state

import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

/**
 * @param stages Pair(stageId, Pair(stageTitle, stageDescription))
 */
data class DetailUiState(
    val init: Boolean = false,
    val loading: Boolean = false,
    val error: String? = null,
    val thumbnail: Pair<String, Uri>? = null,
    val title: String? = null,
    val description: String? = null,
    val stages: SnapshotStateList<Pair<String, Pair<String, String?>>> = mutableStateListOf(),
    val forms: SnapshotStateList<Pair<String, String>> = mutableStateListOf()
)