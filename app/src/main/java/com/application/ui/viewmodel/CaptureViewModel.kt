package com.application.ui.viewmodel

import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CaptureViewModel @Inject constructor(
//    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val sampleImages: SnapshotStateList<Pair<String, Uri>> = mutableStateListOf()

//    init {
//        savedStateHandle.remove<String>(Routes.SAMPLE_STACK_KEY)?.let { imageName ->
//            sampleImages.removeIf { it.first == imageName }
//        }
//    }
}