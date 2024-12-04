package com.application.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.android.utility.state.ResourceState
import com.application.constant.UiStatus
import com.application.data.repository.ProjectRepository
import com.application.data.repository.StageRepository
import com.application.data.repository.UserRepository
import com.application.ui.state.StageDetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.last
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

    fun loadStage(stageId: String) {
        val loggedUser = userRepository.getLoggedUser() ?: throw Error("User doesn't log in.")

        viewModelScope.launch(Dispatchers.IO) {
            stageRepository.getStage(stageId).collectLatest { resourceState ->
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

    fun deleteStage(projectOwnerId: String, stageId: String, successHandler: () -> Unit) {
        if (projectOwnerId.isNullOrEmpty()) {
            _state.update { it.copy(status = UiStatus.ERROR) }
            return
        }
        _state.update { it.copy(status = UiStatus.LOADING) }
        viewModelScope.launch(Dispatchers.IO) {
            stageRepository.deleteStage(stageId = stageId).collectLatest { resourceState ->
                when (resourceState) {
                    is ResourceState.Success -> {
                        viewModelScope.launch { successHandler?.let { successHandler() } }
                    }

                    is ResourceState.Error -> {
                        //val error = resourceState.resId
                        _state.update {
                            it.copy(
                                status = UiStatus.ERROR,
                                error = "Cannot delete stage"
                            )
                        }
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