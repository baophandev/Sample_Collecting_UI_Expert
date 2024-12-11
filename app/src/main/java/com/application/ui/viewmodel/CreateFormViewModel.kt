package com.application.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.R
import com.application.constant.UiStatus
import com.application.data.repository.FormRepository
import com.application.ui.state.CreateFormUiState
import com.sc.library.utility.state.ResourceState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateFormViewModel @Inject constructor(
    val repository: FormRepository
) : ViewModel() {
    private val _state = MutableStateFlow(CreateFormUiState())
    val state = _state.asStateFlow()

    fun initialize() {
        _state.update { it.copy(status = UiStatus.SUCCESS) }
    }

    fun updateTitle(title: String) {
        _state.update { it.copy(title = title) }
    }

    fun submitForm(projectId: String, successHandler: (Boolean) -> Unit) {
        if (!validateFields()) return

        val currentState = state.value

        currentState.fields.removeIf(String::isBlank)
        currentState.fields.forEach(String::trim)

        val collectAction: (ResourceState<String>) -> Unit = { resourceState ->
            when (resourceState) {
                is ResourceState.Error -> _state.update { it.copy(status = UiStatus.ERROR) }

                is ResourceState.Success -> {
                    _state.update { it.copy(status = UiStatus.SUCCESS) }
                    viewModelScope.launch {
                        successHandler(true)
                    }
                }
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            repository.createForm(
                title = currentState.title,
                description = currentState.description,
                projectOwnerId = projectId,
                fields = currentState.fields.toList()
            )
                .onStart { _state.update { it.copy(status = UiStatus.LOADING) } }
                .collectLatest(collectAction)
        }
    }

    private fun validateFields(): Boolean {
        val currentState = state.value
        return if (
            currentState.title.isBlank() ||
            currentState.fields.isEmpty()
        ) {
            _state.update {
                it.copy(
                    status = UiStatus.ERROR,
                    error = R.string.fields_not_validate.toString()
                )
            }
            false
        } else true
    }
}
