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

    fun fetchProject(projectId: String) {
        _state.update { it.copy(status = UiStatus.SUCCESS, projectOwnerId = projectId) }
    }

    fun updateTitle(title: String) {
        _state.update { it.copy(title = title) }
    }

    fun submit(successHandler: () -> Unit) {
        if (!validateFields()) return

        val currentState = state.value

        currentState.fields.removeIf(String::isBlank)
        currentState.fields.forEach(String::trim)

        val collectAction: (ResourceState<String>) -> Unit = { resourceState ->
            when (resourceState) {
                is ResourceState.Error -> _state.update {
                    it.copy(
                        status = UiStatus.SUCCESS,
                        error = resourceState.resId
                    )
                }

                is ResourceState.Success -> {
                    _state.update { CreateFormUiState() }
                    viewModelScope.launch { successHandler() }
                }
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            repository.createForm(
                title = currentState.title,
                description = currentState.description,
                projectOwnerId = currentState.projectOwnerId!!,
                fields = currentState.fields.toList()
            )
                .onStart { _state.update { it.copy(status = UiStatus.LOADING) } }
                .collectLatest(collectAction)
        }
    }

    fun gotError() {
        _state.update { it.copy(error = null) }
    }

    private fun validateFields(): Boolean {
        val currentState = state.value
        if (currentState.title.isBlank()) {
            _state.update { it.copy(error = R.string.error_empty_form_name) }
            return false
        } else if (currentState.fields.isEmpty() || currentState.fields.any { it.isBlank() }) {
            _state.update { it.copy(error = R.string.error_empty_field_name) }
            return false
        } else return true
    }
}
