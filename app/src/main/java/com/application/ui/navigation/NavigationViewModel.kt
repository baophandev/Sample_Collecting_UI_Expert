package com.application.ui.navigation

import androidx.lifecycle.ViewModel
import com.application.ui.state.NavigationState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class NavigationViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(NavigationState())
    val state = _state.asStateFlow()

    fun updateState(newState: NavigationState) {
        _state.update { newState }
    }

}