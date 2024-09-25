package com.application.ui.state

import android.net.Uri
import androidx.annotation.StringRes

data class ModifyProjectUiState(
    val init: Boolean = false,
    val loading: Boolean = false,
    @StringRes val error: Int? = null,
    val thumbnailPath: Pair<String, Uri>? = null,
    val title: String = "",
    val description: String = "",
    val startDate: String? = null,
    val endDate: String? = null,
    val memberIds: List<String> = listOf(),
)
