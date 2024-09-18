package com.application.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.R
import com.application.util.ResourceState
import com.application.data.entity.Stage
import com.application.ui.state.StageUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class CreateStageViewModel @Inject constructor(
) : ViewModel() {

    private val _state = MutableStateFlow(StageUiState())
    val state = _state.asStateFlow()

    fun updateName(title: String) {
        _state.update { it.copy(title = title) }
    }

    fun updateDescription(description: String) {
        _state.update { it.copy(description = description) }
    }

    fun updateDate(date: Long, isStartDate: Boolean) {
        if (isStartDate) {
            _state.update { it.copy(startDate = Date(date)) }
        } else {
            _state.update { it.copy(endDate = Date(date)) }
        }
    }

    fun updateFormId(formId: String) {
        _state.update { it.copy(formId = formId) }
    }

    fun submitStage(projectId: String, successHandler: () -> Unit) {
        if (!validateFields()) return

        val currentState = state.value
        val stage = Stage(
            title = currentState.title,
            description = currentState.description,
            startDate = currentState.startDate!!.time,
            endDate = currentState.endDate!!.time,
            formId = currentState.formId
        )
        val collectAction: (ResourceState<Boolean>) -> Unit = { resourceState ->
            when (resourceState) {
                is ResourceState.Error -> _state.update {
                    it.copy(loading = false, error = resourceState.error)
                }

                is ResourceState.Success -> {
                    _state.update { it.copy(loading = false) }
                    viewModelScope.launch { successHandler() }
                }

                else -> {}
            }
        }

        viewModelScope.launch(Dispatchers.IO) {

        }
    }

    private fun validateFields(): Boolean {
        val currentState = state.value
        return if (
            currentState.title.isBlank() ||
            currentState.startDate == null ||
            currentState.endDate == null) {
            _state.update { it.copy(error = R.string.fields_not_validate) }
            false
        } else if (currentState.startDate > currentState.endDate) {
            _state.update { it.copy(error = R.string.start_date_greater_than_end_date) }
            false
        } else true
    }
}