package com.application.ui.state

import androidx.annotation.StringRes
import com.application.constant.UiStatus
import com.application.data.entity.DynamicField
import com.application.data.entity.Field
import com.application.data.entity.Form

data class CreateSampleUiState(
    val formId: String? = null,
    val fields: List<Field> = listOf(),
    val dynamicFields: List<DynamicField> = listOf(),
    val status: UiStatus = UiStatus.INIT,
    @StringRes val error: Int? = null,
)
