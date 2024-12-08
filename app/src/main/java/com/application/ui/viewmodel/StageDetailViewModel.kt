package com.application.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.application.android.user_library.repository.UserRepository
import com.application.android.utility.state.ResourceState
import com.application.constant.UiStatus
import com.application.data.entity.Sample
import com.application.data.paging.SamplePagingSource
import com.application.data.repository.ProjectRepository
import com.application.data.repository.SampleRepository
import com.application.data.repository.StageRepository
import com.application.ui.state.StageDetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StageDetailViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val stageRepository: StageRepository,
    private val projectRepository: ProjectRepository,
    private val sampleRepository: SampleRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(StageDetailUiState())
    val state = _state.asStateFlow()

    lateinit var flow: Flow<PagingData<Sample>>

    fun loadStage(
        stageId: String,
        skipCached: Boolean = false,
        onComplete: ((Boolean) -> Unit)? = null
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            stageRepository.getStage(stageId, skipCached)
                .onStart { _state.update { it.copy(status = UiStatus.LOADING) } }
                .collectLatest { resourceState ->
                    when (resourceState) {
                        is ResourceState.Error -> _state.update { it.copy(status = UiStatus.ERROR) }
                        is ResourceState.Success -> {
                            val stage = resourceState.data
                            val projectResourceState = projectRepository
                                .getProject(stage.projectOwnerId).last()
                            if (projectResourceState is ResourceState.Success)
                                _state.update {
                                    it.copy(
                                        stage = stage,
                                        projectOwner = projectResourceState.data.owner,
                                        status = UiStatus.SUCCESS
                                    )
                                }
                            else _state.update { it.copy(status = UiStatus.ERROR) }
                        }
                    }
                    onComplete?.let {
                        viewModelScope.launch { onComplete(resourceState is ResourceState.Success) }
                    }
                }
        }

        initSamplePagingFlow(stageId)
    }

    private fun initSamplePagingFlow(stageId: String) {
        if (::flow.isInitialized) return

        flow = Pager(
            androidx.paging.PagingConfig(
                pageSize = 3,
                enablePlaceholders = false,
                prefetchDistance = 1,
                initialLoadSize = 3,
            )
        ) {
            SamplePagingSource(
                stageId = stageId,
                repository = sampleRepository
            )
        }.flow
            .cachedIn(viewModelScope)
            .catch { Log.e(TAG, it.message, it) }
    }

    fun updateStageInDetail(successHandler: (Boolean) -> Unit) {
        val currentStage = state.value.stage!!
        val stageId = currentStage.id

        viewModelScope.launch(Dispatchers.IO) {
            stageRepository.getStage(stageId)
                .onStart { _state.update { it.copy(status = UiStatus.LOADING) } }
                .collectLatest { resourceState ->
                    when (resourceState) {
                        is ResourceState.Error -> _state.update {
                            it.copy(status = UiStatus.ERROR, error = resourceState.resId)
                        }

                        is ResourceState.Success -> {
                            _state.update { it.copy(status = UiStatus.SUCCESS) }
                            viewModelScope.launch {
                                successHandler(true)
                            }
                        }
                    }
                }
        }
    }

    fun isProjectOwner(): Boolean {
        val loggedUser = userRepository.loggedUser
        val projectOwner = state.value.projectOwner
        if (loggedUser == null || projectOwner == null) {
            _state.update { it.copy(status = UiStatus.ERROR) }
            return false
        }
        return loggedUser.id == projectOwner.id
    }

    fun deleteStage(
        projectOwnerId: String,
        stageId: String,
        successHandler: (Boolean) -> Unit
    ) {
        if (projectOwnerId.isEmpty()) {
            _state.update { it.copy(status = UiStatus.ERROR) }
            return
        }
        _state.update { it.copy(status = UiStatus.LOADING) }
        viewModelScope.launch(Dispatchers.IO) {
            stageRepository.deleteStage(stageId = stageId).collectLatest { resourceState ->
                when (resourceState) {
                    is ResourceState.Success -> {
                        viewModelScope.launch { successHandler(true) }
                    }

                    is ResourceState.Error -> _state.update {
                        it.copy(
                            status = UiStatus.ERROR,
                            error = resourceState.resId
                        )
                    }
                }
            }
        }
    }

    fun loadSampleData(sampleId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            sampleRepository.getSample(sampleId)
                .onStart { _state.update { it.copy(status = UiStatus.LOADING) } }
                .collectLatest { resourceState ->
                    when (resourceState) {
                        is ResourceState.Error -> _state.update {
                            it.copy(
                                status = UiStatus.ERROR,
                                error = resourceState.resId
                            )
                        }

                        is ResourceState.Success -> {
                            _state.update {
                                it.copy(
                                    status = UiStatus.SUCCESS,
                                    sample = resourceState.data
                                )
                            }
                        }
                    }
                }
        }
    }

    fun deleteSample(sampleId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            sampleRepository.deleteSample(sampleId)
                .onStart { _state.update { it.copy(status = UiStatus.LOADING) } }
                .collectLatest { }
        }
    }

    companion object {
        const val TAG = "StageDetailViewModel"
    }

}