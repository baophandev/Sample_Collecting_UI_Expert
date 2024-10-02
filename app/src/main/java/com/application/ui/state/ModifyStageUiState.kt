package com.application.ui.state

import androidx.annotation.StringRes

data class ModifyStageUiState(
    val init: Boolean = false,
    val loading: Boolean = false,
    @StringRes val error: Int? = null,

    val title: String = "",
    val description: String = "",
    val startDate: String? = null,
    val endDate: String? = null,
    val memberIds: List<String> = listOf(),
    val formId: String = "",
)
