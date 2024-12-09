package com.application.ui.state

import android.net.Uri
import com.application.android.user_library.entity.User
import com.application.constant.UiStatus
import com.application.data.entity.Sample
import com.application.data.entity.Stage

data class StageDetailUiState(
    val status: UiStatus = UiStatus.INIT,
    val projectOwner: User? = null,
    val error: Int? = null,
    val sample: Sample? = null,
    val stage: Stage? = null,
    val thumbnail: Uri? = null
)
