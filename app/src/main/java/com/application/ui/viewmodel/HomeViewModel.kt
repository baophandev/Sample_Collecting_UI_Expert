package com.application.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.ui.state.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState(init = true))
    val state = _state.asStateFlow()

    fun getProjects(userEmail: String, refreshSuccess: (() -> Unit)? = null) {
        if (refreshSuccess == null) {
            _state.update { it.copy(init = false) }
        }

        viewModelScope.launch(Dispatchers.IO) {
        }
    }

    companion object {
        const val TAG = "HomeViewModel"
    }
}