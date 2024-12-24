package com.application.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.application.constant.UiStatus
import com.application.data.paging.MessagePagingSource
import com.application.ui.state.ChatUiState
import com.sc.library.chat.data.entity.ReceivingMessage
import com.sc.library.chat.data.entity.SendingMessage
import com.sc.library.chat.data.repository.ConversationRepository
import com.sc.library.chat.data.repository.MessageRepository
import com.sc.library.user.repository.UserRepository
import com.sc.library.utility.state.ResourceState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val conversationRepository: ConversationRepository,
    private val messageRepository: MessageRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ChatUiState())
    val state = _state.asStateFlow()

    private val _messages = MutableStateFlow<List<ReceivingMessage>>(emptyList())
    val messages = _messages.asStateFlow()

    lateinit var messagePagingFlow: Flow<PagingData<ReceivingMessage>>

    fun fetchMessages(conversationId: Long) {
        _state.update {
            it.copy(
                status = UiStatus.LOADING,
                text = "",
            )
        }

        viewModelScope.launch(Dispatchers.IO) {
            when (val rsState = conversationRepository
                .getConversation(conversationId = conversationId).last()) {
                is ResourceState.Error -> _state.update {
                    it.copy(
                        status = UiStatus.ERROR,
                        error = rsState.resId
                    )
                }

                is ResourceState.Success -> _state.update {
                    it.copy(
                        status = UiStatus.SUCCESS,
                        sendingStatus = UiStatus.SUCCESS,
                        conversation = rsState.data
                    )
                }
            }
        }

        initMessagePagingFlow(conversationId)
        subscribeConversation(conversationId)
    }

    fun updateMessage(text: String) {
        _state.update { it.copy(text = text) }
    }

    fun sendMessage() {
        val currentState = state.value
        val conversation = currentState.conversation ?: return

        viewModelScope.launch(Dispatchers.IO) {
            _state.update { it.copy(sendingStatus = UiStatus.LOADING) }

            val message = SendingMessage(
                text = currentState.text,
                attachments = currentState.selectedAttachments
            )
            when (val rsState = messageRepository.sendMessage(conversation.id, message).last()) {
                is ResourceState.Error -> _state.update {
                    it.copy(
                        sendingStatus = UiStatus.ERROR,
                        error = rsState.resId
                    )
                }

                is ResourceState.Success -> {
                    val onComplete = {
                        _state.update {
                            it.copy(
                                sendingStatus = UiStatus.SUCCESS,
                                text = ""
                            )
                        }
                    }
                    val onError: (Throwable) -> Unit = { exception ->
                        Log.e(TAG, exception.message, exception)
                        _state.update {
                            it.copy(
                                sendingStatus = UiStatus.ERROR,
                                error = null
                            )
                        }
                    }

                    rsState.data
                        .timeout(50000, TimeUnit.MILLISECONDS)
                        .subscribe(onComplete, onError)
                }
            }
        }
    }

    fun isSendingMessage(senderId: String): Boolean {
        val userId = userRepository.loggedUser?.id ?: return false
        return senderId == userId
    }

    private fun initMessagePagingFlow(conversationId: Long) {
        messagePagingFlow = Pager(
            PagingConfig(
                pageSize = 8,
                enablePlaceholders = false,
                initialLoadSize = 10,
            )
        ) {
            MessagePagingSource(
                conversationId = conversationId,
                messageRepository = messageRepository
            )
        }.flow
            .cachedIn(viewModelScope)
            .catch { Log.e(TAG, it.message, it) }
    }

    private fun subscribeConversation(conversationId: Long) {
        messageRepository.subscribeConversation(
            conversationId = conversationId,
            incomingHandler = { newMessage ->
                _messages.update {
                    val current = it.toMutableList()
                    current.add(newMessage)
                    current
                }
            },
            errorHandler = { exception -> Log.e(TAG, exception.message, exception) })
    }

    companion object {
        const val TAG = "ChatViewModel"
    }

}
