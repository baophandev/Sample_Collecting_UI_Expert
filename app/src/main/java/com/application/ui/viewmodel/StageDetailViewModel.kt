package com.application.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.constant.UiStatus
import com.application.data.repository.ProjectRepository
import com.application.ui.state.StageDetailUiState
import com.application.util.ResourceState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StageDetailViewModel @Inject constructor(
    private val repository: ProjectRepository
) : ViewModel() {
    private val _state = MutableStateFlow(StageDetailUiState())
    val state = _state.asStateFlow()

    fun loadStage(stageId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getStage(stageId).collectLatest { resourceState ->
                when (resourceState) {
                    is ResourceState.Error -> _state.update { it.copy(status = UiStatus.ERROR) }
                    is ResourceState.Success -> _state.update {
                        it.copy(stage = resourceState.data, status = UiStatus.SUCCESS)
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

    fun deleteStage(projectId: String, stageId: String, successHandler: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
        }
    }

    fun deleteSample(projectId: String, stageId: String, sampleId: String) {
        viewModelScope.launch(Dispatchers.IO) {
        }
    }

}