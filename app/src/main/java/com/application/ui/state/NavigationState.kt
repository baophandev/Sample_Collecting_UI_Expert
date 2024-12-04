package com.application.ui.state

import android.net.Uri
import com.application.constant.ReloadSignal

data class NavigationState(
    val reloadSignal: ReloadSignal = ReloadSignal.NONE,
    val loggedInUserId: String? = null,
    val currentProjectId:String? = null,
    val currentStageId: String? = null,
    val currentFormId: String? = null,
    val newSample: Pair<String, Uri>? = null
)
