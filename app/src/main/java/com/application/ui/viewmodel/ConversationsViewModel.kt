package com.application.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.application.constant.ReloadSignal
import com.application.data.paging.ConversationPagingSource
import io.github.nhatbangle.sc.chat.data.entity.Conversation
import io.github.nhatbangle.sc.chat.data.repository.ConversationRepository
import io.github.nhatbangle.sc.user.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import javax.inject.Inject

@HiltViewModel
class ConversationsViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val conversationRepository: ConversationRepository
) : ViewModel() {

    var conversationsFlow: Flow<PagingData<Conversation>> = initFlow()

    fun reload(signal: ReloadSignal) {
        when (signal) {
            ReloadSignal.RELOAD_ALL_CONVERSATIONS -> conversationsFlow = initFlow()
            else -> {}
        }
    }

    private fun initFlow(): Flow<PagingData<Conversation>> = Pager(
        PagingConfig(
            pageSize = 8,
            enablePlaceholders = false,
            prefetchDistance = 1,
            initialLoadSize = 3,
        )
    ) {
        ConversationPagingSource(
            userRepository = userRepository,
            conversationRepository = conversationRepository
        )
    }.flow
        .cachedIn(viewModelScope)
        .catch { Log.e(TAG, it.message, it) }

    companion object {
        const val TAG = "ConversationsViewModel"
    }

}
