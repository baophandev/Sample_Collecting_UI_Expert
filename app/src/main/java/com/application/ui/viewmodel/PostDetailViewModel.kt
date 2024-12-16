package com.application.ui.viewmodel

import android.app.Application
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.application.constant.UiStatus
import com.application.data.entity.FileInPost
import com.application.data.paging.FileInPostPagingSource
import com.application.data.repository.PostRepository
import com.application.ui.state.PostDetailUiState
import com.sc.library.utility.state.ResourceState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostDetailViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val application: Application
) : ViewModel() {

    private val _state = MutableStateFlow(PostDetailUiState())
    val state = _state.asStateFlow()

    lateinit var filesFlow: Flow<PagingData<FileInPost>>

    fun fetchPost(postId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            postRepository.getPost(postId = postId)
                .onStart { _state.update { it.copy(status = UiStatus.LOADING) } }
                .collectLatest { rsState ->
                    when (rsState) {
                        is ResourceState.Error -> _state.update { it.copy(status = UiStatus.ERROR) }
                        is ResourceState.Success -> _state.update {
                            it.copy(status = UiStatus.SUCCESS, post = rsState.data)
                        }
                    }
                }
        }

        filesFlow = Pager(
            PagingConfig(
                pageSize = 6,
                enablePlaceholders = false,
                prefetchDistance = 1,
                initialLoadSize = 6,
            )
        ) { FileInPostPagingSource(postId = postId, postRepository = postRepository) }
            .flow
            .cachedIn(viewModelScope)
            .catch { exception -> Log.e(TAG, exception.message, exception) }
    }

    fun startDownload(uri: Uri, fileName: String) {
        val downloadManager =
            application.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        val request = DownloadManager.Request(uri)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)

        downloadManager.enqueue(request)
    }

    companion object {
        const val TAG = "PostDetailViewModel"
    }

}