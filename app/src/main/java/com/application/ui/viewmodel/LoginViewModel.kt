package com.application.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.R
import com.application.constant.UiStatus
import com.application.ui.state.LoginUiState
import com.sc.library.chat.data.repository.MessageRepository
import com.sc.library.user.repository.UserRepository
import com.sc.library.utility.state.ResourceState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val messageRepository: MessageRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = _state.asStateFlow()

    private val requiredScopes = listOf("expert")

    fun login(loginSuccessful: () -> Unit) {
        val currentState = state.value

        if (!validateFields()) {
            _state.update { it.copy(error = R.string.login_info_not_blank) }
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            userRepository.login(
                username = currentState.username,
                password = currentState.password,
                requiredScopes = requiredScopes
            )
                .onStart { _state.update { it.copy(status = UiStatus.LOADING) } }
                .collectLatest { result ->
                    when (result) {
                        is ResourceState.Success -> {
                            val userId = userRepository.loggedUser?.id!!
                            when (val rsState =
                                messageRepository.connectChatServer(userId).last()) {
                                is ResourceState.Error -> _state.update {
                                    it.copy(
                                        status = UiStatus.ERROR,
                                        error = rsState.resId
                                    )
                                }

                                is ResourceState.Success -> {
                                    _state.update { it.copy(status = UiStatus.SUCCESS) }
                                    viewModelScope.launch { loginSuccessful() }
                                }
                            }
                        }

                        is ResourceState.Error -> {
                            _state.update {
                                it.copy(
                                    status = UiStatus.ERROR,
                                    error = result.resId
                                )
                            }
                        }

                    }
                }
        }
    }

    fun updateUsername(newUserName: String) {
        _state.update { it.copy(username = newUserName) }
    }

    fun updatePassword(newPassword: String) {
        _state.update { it.copy(password = newPassword) }
    }

    private fun validateFields(): Boolean {
        val currentState = state.value
        return currentState.username.isNotBlank() && currentState.password.isNotBlank()
    }

}
