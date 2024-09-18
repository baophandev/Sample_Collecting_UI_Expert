package com.application.ui.state

import androidx.annotation.StringRes

data class ModifyStageUiState(
    val init: Boolean = false,
    val loading: Boolean = false,
    @StringRes val error: Int? = null,

    val title: String = "",
    val description: String = "",
    val startDate: Long? = null,
    val endDate: Long? = null,
    val memberIds: List<String> = listOf(),
    val formId: String = "",
)
