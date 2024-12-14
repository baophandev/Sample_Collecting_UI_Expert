package com.application.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.application.constant.ProjectQueryType
import com.application.constant.ProjectStatus
import com.application.data.paging.ProjectPagingSource
import com.application.data.repository.ProjectRepository
import com.sc.library.user.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val projectRepository: ProjectRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val query = ProjectQueryType.ALL
    private val status = ProjectStatus.NORMAL

    val flow = Pager(
        PagingConfig(
            pageSize = 3,
            enablePlaceholders = false,
            prefetchDistance = 1,
            initialLoadSize = 3,
        )
    ) {
        ProjectPagingSource(
            query = query,
            status = status,
            projectRepository = projectRepository,
            userRepository = userRepository
        )
    }.flow
        .cachedIn(viewModelScope)
        .catch { Log.e(TAG, it.message, it) }

    companion object {
        const val TAG = "HomeViewModel"
    }

}