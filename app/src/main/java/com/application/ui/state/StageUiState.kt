package com.application.ui.state

import androidx.annotation.StringRes
import java.util.Date

data class StageUiState(
    val loading: Boolean = false,
    @StringRes val error: Int? = null,

    val title: String = "",
    val description: String = "",
    val startDate: Date? = null,
    val endDate: Date? = null,
    val formId: String = "",
    val emailMembers: List<String> = listOf(),
)
