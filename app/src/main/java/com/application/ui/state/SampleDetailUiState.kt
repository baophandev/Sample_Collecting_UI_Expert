package com.application.ui.state

import com.application.constant.UiStatus
import com.application.data.entity.Sample

data class SampleDetailUiState(
    val status: UiStatus = UiStatus.INIT,
    val error: Int? = null,
    val sample: Sample? = null
)
