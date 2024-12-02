package com.application.ui.state

import com.application.constant.ReloadSignal

data class NavigationState(
    val reloadSignal: ReloadSignal = ReloadSignal.NONE
)
