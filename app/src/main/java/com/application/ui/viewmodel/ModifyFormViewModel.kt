package com.application.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.util.ResourceState
import com.application.data.entity.Form
import com.application.ui.state.ModifyFormUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ModifyFormViewModel @Inject constructor(
) : ViewModel() {
    private val _state = MutableStateFlow(ModifyFormUiState(init = true))
    val state = _state.asStateFlow()

//    fun setModifiedForm(form: Form) {
//        form.fields?.map { it.value }?.let {
//            state.value.fields.clear()
//            state.value.fields.addAll(it)
//        }
//        _state.update { it.copy(name = form.name!!, init = false) }
//    }

    fun updateTitle(title: String) {
        _state.update { it.copy(name = title) }
    }

    fun submit(preForm: Pair<String, Form>, projectId: String, successHandler: () -> Unit) {
        val collectAction: (ResourceState<Boolean>) -> Unit = { resourceState ->
            when (resourceState) {
                is ResourceState.Success -> viewModelScope.launch { successHandler() }

                is ResourceState.Error -> _state.update {
                    it.copy(loading = false, error = resourceState.resId)
                }

                else -> {}
            }
        }

        val currentState = state.value
        //val newName = if (currentState.name != preForm.second.name) currentState.name else null
        val newFields = currentState.fields.toList()

        viewModelScope.launch(Dispatchers.IO) {
        }
    }
}
