package com.application.ui.state

import android.net.Uri
import androidx.annotation.StringRes
import com.application.constant.UiStatus
import com.application.data.entity.Answer
import com.application.data.entity.DynamicField

data class CreateSampleUiState(
    val formId: String? = null,
    val sampleImage: Pair<String, Uri>? = null,
    val answers: List<Answer> = listOf(),
    val dynamicFields: List<DynamicField> = listOf(),
    val status: UiStatus = UiStatus.INIT,
    @StringRes val error: Int? = null,
)
