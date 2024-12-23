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
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val conversationRepository: ConversationRepository,
    private val messageRepository: MessageRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ChatUiState())
    val state = _state.asStateFlow()

    lateinit var messagesFlow: Flow<PagingData<ReceivingMessage>>

    fun fetchMessages(conversationId: Long) {
        _state.update { it.copy(status = UiStatus.LOADING, text = "") }

        viewModelScope.launch {
            when (val rsState =
                conversationRepository.getConversation(conversationId = conversationId).last()) {
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

        messagesFlow = Pager(
            PagingConfig(
                pageSize = 8,
                enablePlaceholders = false,
                prefetchDistance = 1,
                initialLoadSize = 3,
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

                is ResourceState.Success -> rsState.data
                    .doOnComplete {
                        _state.update {
                            it.copy(
                                sendingStatus = UiStatus.SUCCESS,
                                text = ""
                            )
                        }
                    }
                    .doOnError { exception ->
                        Log.e(TAG, exception.message, exception)
                        _state.update {
                            it.copy(
                                sendingStatus = UiStatus.ERROR,
                                error = null
                            )
                        }
                    }.blockingAwait()
            }
        }
    }

    companion object {
        const val TAG = "ChatViewModel"
    }

}
