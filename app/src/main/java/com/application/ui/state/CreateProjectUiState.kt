package com.application.ui.state

import android.net.Uri
import androidx.annotation.StringRes

data class CreateProjectUiState(
    val thumbnail: Uri? = null,
    val title: String = "",
    val description: String = "",
    val startDate: String? = null,
    val endDate: String? = null,
    val memberIds: List<String> = emptyList(),
    val memberEmailMap: Map<String, String> = emptyMap(), // key: email -> value: memberId
    val loading: Boolean = false,
    @StringRes val error: Int? = null
)
