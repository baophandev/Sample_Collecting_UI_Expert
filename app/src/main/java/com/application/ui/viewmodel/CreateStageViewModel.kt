package com.application.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.R
import com.application.android.utility.state.ResourceState
import com.application.constant.UiStatus
import com.application.data.repository.FormRepository
import com.application.data.repository.StageRepository
import com.application.ui.state.CreateStageUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateStageViewModel @Inject constructor(
    private val stageRepository: StageRepository,
    private val formRepository: FormRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CreateStageUiState())
    val state = _state.asStateFlow()

    fun initialize(projectId: String) {
        getAllForms(projectId)
    }

    private fun getAllForms(projectId: String) {
        _state.update { it.copy(status = UiStatus.LOADING) }
        viewModelScope.launch(Dispatchers.IO) {
            formRepository.getAllForms(projectId).collectLatest { resourceState ->
                when (resourceState) {
                    is ResourceState.Success -> _state.update {
                        it.copy(
                            status = UiStatus.SUCCESS,
                            forms = resourceState.data
                        )
                    }

                    is ResourceState.Error -> _state.update { it.copy(status = UiStatus.ERROR) }
                }
            }
        }
    }

    fun updateName(title: String) {
        _state.update { it.copy(name = title) }
    }

    fun updateDescription(description: String) {
        _state.update { it.copy(description = description) }
    }

    fun updateDate(date: String, isStartDate: Boolean) {
        if (isStartDate) {
            _state.update { it.copy(startDate = date) }
        } else {
            _state.update { it.copy(endDate = date) }
        }
    }

    fun selectForm(formIdx: Int) {
        val form = state.value.forms[formIdx]
        _state.update { it.copy(selectedForm = Pair(form.id, form.title)) }
    }

    fun submitStage(projectId: String, formId: String, successHandler: (Boolean) -> Unit) {
        if (!validateFields()) return
        val currentState = state.value

        val collectAction: (ResourceState<String>) -> Unit = { resourceState ->
            when (resourceState) {
                is ResourceState.Error -> _state.update {
                    it.copy(status = UiStatus.SUCCESS, error = resourceState.resId)
                }

                is ResourceState.Success -> {
                    _state.update { it.copy(status = UiStatus.SUCCESS) }
                    viewModelScope.launch { successHandler(true) }
                }
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            stageRepository.createStage(
                name = currentState.name,
                description = currentState.description,
                startDate = currentState.startDate,
                endDate = currentState.endDate,
                formId = formId,
                projectOwnerId = projectId
            ).collectLatest(collectAction)
        }
    }

    fun gotError() {
        _state.update { it.copy(error = null) }
    }

    private fun validateFields(): Boolean {
        val currentState = state.value
        if (currentState.name.isBlank()) {
            _state.update { it.copy(error = R.string.error_empty_stage_name) }
            return false
        }
        else if (currentState.startDate == null || currentState.endDate == null) {
            _state.update { it.copy(error = R.string.error_empty_startDate_endDate) }
            return false
        }
        else if (currentState.startDate > currentState.endDate) {
            _state.update { it.copy(error = R.string.error_start_date_greater_than_end_date) }
            return false
        } else return true
    }
}