package com.application.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.application.constant.ProjectQueryType
import com.application.constant.ProjectStatus
import com.application.constant.ReloadSignal
import com.application.data.entity.Project
import com.application.data.paging.ProjectPagingSource
import com.application.data.repository.ProjectRepository
import com.sc.library.user.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val projectRepository: ProjectRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val query = ProjectQueryType.ALL
    private val status = ProjectStatus.NORMAL

    var flow: Flow<PagingData<Project>> = initFlow()

    init {
        initFlow()
    }

    fun reload(reloadProject: ReloadSignal) {
        when (reloadProject) {
            ReloadSignal.RELOAD_ALL_PROJECTS -> flow = initFlow()
            else -> {}
        }
    }

    private fun initFlow(): Flow<PagingData<Project>> {
        return Pager(
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
    }

    companion object {
        const val TAG = "HomeViewModel"
    }

}