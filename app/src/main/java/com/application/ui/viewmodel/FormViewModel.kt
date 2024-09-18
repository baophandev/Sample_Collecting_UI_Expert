package com.application.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.R
import com.application.util.ResourceState
import com.application.data.entity.Form
import com.application.ui.state.FormUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FormViewModel @Inject constructor(
) : ViewModel() {
    private val _state = MutableStateFlow(FormUiState())
    val state = _state.asStateFlow()

    fun updateTitle(title: String) {
        _state.update { it.copy(title = title) }
    }

    fun submitForm(projectId: String, successHandler: () -> Unit) {
        if (!validateFields()) return

        val currentState = state.value
        currentState.fields.removeIf(String::isBlank)
        currentState.fields.forEach(String::trim)
        val form = Form(name = currentState.title)
        val collectAction: (ResourceState<Pair<String, Form>>) -> Unit = { resourceState ->
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
            currentState.fields.isEmpty()
        ) {
            _state.update { it.copy(error = R.string.fields_not_validate) }
            false
        } else true
    }
}
