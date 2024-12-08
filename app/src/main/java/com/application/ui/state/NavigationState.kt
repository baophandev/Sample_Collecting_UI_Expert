package com.application.ui.state

import android.net.Uri
import com.application.constant.ReloadSignal

data class NavigationState(
    val reloadSignal: ReloadSignal = ReloadSignal.NONE,
    val currentProjectId:String? = null,
    val currentStageId: String? = null,
    val currentFormId: String? = null,
    val newSample: Pair<String, Uri>? = null,
    val currentSampleId: String? = null
)
