package com.application.ui.state

import android.net.Uri
import androidx.annotation.StringRes
import com.application.constant.UiStatus
import com.application.data.entity.Sample
import com.application.data.entity.Stage
import com.sc.library.user.entity.User

data class StageDetailUiState(
    val status: UiStatus = UiStatus.INIT,
    val projectOwner: User? = null,
    @StringRes val error: Int? = null,
    val sample: Sample? = null,
    val stage: Stage? = null,
    val thumbnail: Uri? = null
)
