package com.application.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.android.user_library.repository.UserRepository
import com.application.android.utility.state.ResourceState
import com.application.constant.UiStatus
import com.application.data.repository.ProjectRepository
import com.application.data.repository.StageRepository
import com.application.ui.state.StageDetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StageDetailViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val projectRepository: ProjectRepository,
    private val stageRepository: StageRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(StageDetailUiState())
    val state = _state.asStateFlow()

    fun loadStage(
        stageId: String,
        skipCached: Boolean = false,
        onComplete: ((Boolean) -> Unit)? = null
    ) {
        val loggedUser = userRepository.loggedUser ?: throw Error("User doesn't log in.")

        _state.update { it.copy(status = UiStatus.LOADING) }
        viewModelScope.launch(Dispatchers.IO) {
            stageRepository.getStage(stageId, skipCached).collectLatest { resourceState ->
                when (resourceState) {
                    is ResourceState.Error -> _state.update { it.copy(status = UiStatus.ERROR) }
                    is ResourceState.Success -> _state.update {
                        val stage = resourceState.data
                        val checkState = projectRepository
                            .isProjectOwner(loggedUser.id, stage.projectOwnerId).last()
                        val isProjectOwner = when (checkState) {
                            is ResourceState.Error -> throw Error(checkState.message)
                            is ResourceState.Success -> checkState.data
                        }
                        it.copy(
                            isProjectOwner = isProjectOwner,
                            stage = stage,
                            status = UiStatus.SUCCESS
                        )
                    }
                }
                onComplete?.let {
                    viewModelScope.launch { onComplete(resourceState is ResourceState.Success) }
                }
            }
        }
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
                            it.copy(status = UiStatus.ERROR, error = "Cannot get stage")
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

    fun setCurrentStage(stageId: String, projectId: String) {
        _state.update { it.copy() }

        viewModelScope.launch(Dispatchers.IO) {

        }
    }

    fun loadSampleData(projectId: String, stageId: String, sampleId: String) {
        viewModelScope.launch(Dispatchers.IO) {

        }
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

                    is ResourceState.Error -> {
                        //val error = resourceState.resId
                        _state.update { it.copy(status = UiStatus.ERROR, error = "Cannot delete stage") }
                    }
                }
            }
        }
    }

    fun deleteSample(projectId: String, stageId: String, sampleId: String) {
        viewModelScope.launch(Dispatchers.IO) {
        }
    }

}