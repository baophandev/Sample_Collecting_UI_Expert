package com.application.ui.state

import androidx.annotation.StringRes
import com.application.constant.UiStatus
import com.application.data.entity.Field
import com.application.data.entity.Form

data class ModifyFormUiState(
    val status: UiStatus = UiStatus.INIT,
    @StringRes val error: Int? = null,
    val form: Form? = null,
    val fields: List<Field> = emptyList(),
    val addedFieldIds: List<String> = emptyList(),
    val updatedFieldIds: Set<String> = emptySet(),
    val deletedFieldIds: List<String> = emptyList(),
    val isUpdated: Boolean = false
)
