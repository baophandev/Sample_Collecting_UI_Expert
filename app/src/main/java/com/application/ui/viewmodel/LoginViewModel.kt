package com.application.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.R
import com.application.constant.UiStatus
import com.application.ui.state.LoginUiState
import io.github.nhatbangle.sc.chat.data.datasource.IChatService
import io.github.nhatbangle.sc.user.repository.UserRepository
import io.github.nhatbangle.sc.utility.state.ResourceState
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
//    @ApplicationContext private val context: Context,
    private val userRepository: UserRepository,
    private val chatService: IChatService,
//    private val messageRepository: MessageRepository
) : ViewModel() {

    private val requiredScopes = listOf("expert")

    private val _state = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = _state.asStateFlow()

    private val notificationDisposable: Disposable? = null

    override fun onCleared() {
        logout()
        super.onCleared()
    }

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
                            connectChatServer(loginSuccessful)
                            subscribeNotification()
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

    private fun connectChatServer(loginSuccessful: () -> Unit) {
        val onComplete: () -> Unit = {
            _state.update {
                it.copy(
                    status = UiStatus.SUCCESS,
                    password = ""
                )
            }
            viewModelScope.launch { loginSuccessful() }
        }
        val onError: (Throwable) -> Unit = { exception ->
            Log.e(TAG, exception.message, exception)
            _state.update {
                it.copy(
                    status = UiStatus.ERROR,
                    error = io.github.nhatbangle.sc.R.string.connect_server_error
                )
            }
        }
        chatService.run {
            connectWebSocketServer(userRepository.loggedUser?.id!!)
                .timeout(60, TimeUnit.SECONDS)
                .subscribe(onComplete, onError)
        }
    }

    private fun subscribeNotification() {
//        var builder = NotificationCompat.Builder(
//            context,
//            MainActivity.NOTIFY_CHAT_CHANNEL_ID
//        )
//            .setSmallIcon(R.drawable.splash_logo)
//            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//
//        messageRepository.subscribeNotification(
//            incomingHandler = {
//                builder = builder.setContentTitle(it.sender.name)
//                    .setContentText(it.content)
//            },
//            errorHandler = {}
//        )
    }

    fun logout() {
        userRepository.loggedUser?.let {
            chatService.disconnectWebSocketServer(it.id)
            userRepository.logout()
        }
        notificationDisposable?.dispose()
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

    companion object {
        const val TAG = "LoginViewModel"
    }

}
