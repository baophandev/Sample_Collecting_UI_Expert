package com.application.ui.state

import androidx.annotation.StringRes

data class StageUiState(
    val loading: Boolean = false,
    @StringRes val error: Int? = null,

    val title: String = "",
    val description: String = "",
    val startDate: String? = null,
    val endDate: String? = null,
    val formId: String = "",
    val emailMembers: List<String> = listOf(),
)
