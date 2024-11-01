package com.application.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.application.data.paging.ProjectPagingSource
import com.application.data.datasource.IProjectService
import com.application.data.repository.AttachmentRepository
import com.application.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val projectService: IProjectService,
    private val userRepository: UserRepository,
    private val attachmentRepository: AttachmentRepository
) : ViewModel() {

    val flow = Pager(
        PagingConfig(
            pageSize = 3,
            enablePlaceholders = false,
            prefetchDistance = 1,
            initialLoadSize = 3,
        )
    ) {
        ProjectPagingSource(projectService, userRepository, attachmentRepository)
    }.flow
        .cachedIn(viewModelScope)
        .catch { Log.e(TAG, it.message, it) }

    companion object {
        const val TAG = "HomeViewModel"
    }

}