package com.application.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.ui.state.StageDetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StageDetailViewModel @Inject constructor(
) : ViewModel() {
    private val _state = MutableStateFlow(StageDetailUiState(init = true))
    val state = _state.asStateFlow()

    fun setCurrentStage(stageId: String, projectId: String) {
        _state.update { it.copy(init = false, loading = true) }

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