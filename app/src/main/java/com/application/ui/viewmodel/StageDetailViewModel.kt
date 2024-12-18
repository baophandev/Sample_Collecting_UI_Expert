package com.application.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.application.constant.ReloadSignal
import com.application.constant.UiStatus
import com.application.data.entity.Sample
import com.application.data.paging.SamplePagingSource
import com.application.data.repository.ProjectRepository
import com.application.data.repository.SampleRepository
import com.application.data.repository.StageRepository
import com.application.ui.state.StageDetailUiState
import com.sc.library.user.repository.UserRepository
import com.sc.library.utility.state.ResourceState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
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

    lateinit var sampleFlow: Flow<PagingData<Sample>>

    fun loadStage(
        stageId: String,
        skipCached: Boolean = false,
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
                }
        }

        initSamplePagingFlow(stageId)
    }

    private fun initSamplePagingFlow(stageId: String) {
        sampleFlow = Pager(
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

    fun deleteStage(successHandler: (Boolean) -> Unit) {
        state.value.stage?.id?.let { stageId ->
            viewModelScope.launch(Dispatchers.IO) {
                stageRepository.deleteStage(stageId = stageId)
                    .onStart { _state.update { it.copy(status = UiStatus.LOADING) } }
                    .collectLatest { resourceState ->
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
    }

    fun deleteSamples(sampleIds: List<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            _state.update { it.copy(status = UiStatus.LOADING) }

            val results = sampleIds.map { id ->
                async {
                    val result = sampleRepository.deleteSample(id).last()
                    result is ResourceState.Success
                }
            }.awaitAll()

            _state.update { currentState ->
                currentState.copy(
                    status = if (results.all { it }) UiStatus.SUCCESS else UiStatus.ERROR
                )
            }
        }
    }

    fun reload(signal: ReloadSignal) {
        when (signal) {
            ReloadSignal.RELOAD_ALL_SAMPLES -> initSamplePagingFlow(state.value.stage!!.id)
            ReloadSignal.RELOAD_STAGE -> loadStage(
                stageId = state.value.stage!!.id,
                skipCached = true
            )

            else -> {}
        }
    }

    companion object {
        const val TAG = "StageDetailViewModel"
    }

}