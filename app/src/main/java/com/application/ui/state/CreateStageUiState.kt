package com.application.ui.state

import androidx.annotation.StringRes
import com.application.constant.UiStatus
import com.application.data.entity.Form

data class CreateStageUiState(
    val status: UiStatus = UiStatus.INIT,
    @StringRes val error: Int? = null,

    val name: String = "",
    val description: String = "",
    val startDate: String? = null,
    val endDate: String? = null,

    val forms: List<Form> = emptyList(),
    val selectedForm: Pair<String, String>? = null, // <Form ID, Form title>
    val emailMembers: List<String> = emptyList(),
)