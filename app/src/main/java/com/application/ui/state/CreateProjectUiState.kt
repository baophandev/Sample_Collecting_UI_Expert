package com.application.ui.state

import android.net.Uri
import androidx.annotation.StringRes
import java.time.LocalDate

data class CreateProjectUiState(
    val thumbnail: Uri? = null,
    val name: String = "",
    val description: String = "",
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val memberIds: List<String> = listOf(),
    val loading: Boolean = false,
    @StringRes val error: Int? = null
)
