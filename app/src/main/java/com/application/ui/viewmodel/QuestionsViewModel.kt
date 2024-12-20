package com.application.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.application.constant.ReloadSignal
import com.application.data.entity.Post
import com.application.data.paging.PostPagingSource
import com.application.data.repository.PostRepository
import com.application.ui.state.QuestionsUiState
import com.sc.library.user.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class QuestionsViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val postRepository: PostRepository
) : ViewModel() {

    lateinit var postFlow: Flow<PagingData<Post>>
    private var isAnswered = false
    private var title = ""

    private val _state = MutableStateFlow(QuestionsUiState(isAnswered = isAnswered, title = title))
    val state = _state.asStateFlow()

    private lateinit var debounceFlow: Flow<QuestionsUiState>

    @OptIn(FlowPreview::class)
    suspend fun initCheckAnsweredDebounce(refreshCallback: () -> Unit) {
        debounceFlow = _state.debounce(300)
        debounceFlow.collect {
            if (it.isAnswered != isAnswered) {
                isAnswered = it.isAnswered
                refreshCallback()
            }
            if (it.title != title) {
              title = it.title
              refreshCallback()
            }
        }
    }

    fun reload(reloadProject: ReloadSignal) {
        when (reloadProject) {
            ReloadSignal.RELOAD_ALL_POSTS -> postFlow = initFlow()
            else -> {}
        }
    }

    fun updateIsAnswered(isAnswered: Boolean) {
        _state.update { it.copy(isAnswered = isAnswered) }
    }

    fun searchPost(title: String) {
        _state.update { it.copy(title = title) }
    }

    private fun initFlow(): Flow<PagingData<Post>> {
        return Pager(
            PagingConfig(
                pageSize = 3,
                enablePlaceholders = false,
                prefetchDistance = 1,
                initialLoadSize = 3,
            )
        ) {
            PostPagingSource(
                isAnswered = isAnswered,
                title = title,
                postRepository = postRepository,
                userRepository = userRepository
            )
        }.flow
            .cachedIn(viewModelScope)
            .catch { Log.e(TAG, it.message, it) }
    }

    companion object {
        const val TAG = "QuestionsViewModel"
    }

}
