package com.application.ui.viewmodel

import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CaptureViewModel @Inject constructor(
) : ViewModel() {

    val sampleImages: SnapshotStateList<Uri> = mutableStateListOf()

    lateinit var stageId: String

    fun loadStage(stageId: String) {
        this.stageId = stageId
    }

    fun removeCreatedSampleImage(uri: Uri) {
        sampleImages.remove(uri)
    }

}