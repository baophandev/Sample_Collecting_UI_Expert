package com.application.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.application.data.entity.User
import com.application.ui.state.LoginUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
) : ViewModel() {

    private val _state = MutableStateFlow(LoginUiState(loading = true))
    val state = _state.asStateFlow()

    fun reInitScreen() {
        _state.update { LoginUiState() }
    }

    fun autoLogin(successHandler: (User) -> Unit) {
        if (!state.value.loading) return
    }

}
