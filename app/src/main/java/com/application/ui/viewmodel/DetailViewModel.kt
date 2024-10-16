package com.application.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.constant.UiStatus
import com.application.data.repository.ProjectRepository
import com.application.ui.state.DetailUiState
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
class DetailViewModel @Inject constructor(
    private val repository: ProjectRepository
) : ViewModel() {

    // luu vao day
    private val _state = MutableStateFlow(DetailUiState())
    val state = _state.asStateFlow()

    fun loadProject(projectId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getProject(projectId).collectLatest { resourceState ->
                when (resourceState) {
                    is ResourceState.Error -> _state.update { it.copy(status = UiStatus.ERROR) }
                    is ResourceState.Success -> _state.update {
                        it.copy(project = resourceState.data, status = UiStatus.SUCCESS)
                    }
                }
            }
        }
    }

    fun deleteProject(
        projectId: String,
        emailMembers: List<String>? = null,
        successHandler: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
        }
    }

    fun deleteForm(projectId: String, formId: String) {
        viewModelScope.launch(Dispatchers.IO) {
        }
    }

    fun getStages(projectId: String, successHandler: (() -> Unit)?= null){
        _state.update { it.copy(status = UiStatus.LOADING) }
        viewModelScope.launch(Dispatchers.IO){
            repository.getAllStage(projectId).collectLatest { resourceState ->
                when(resourceState) {
                    is ResourceState.Success -> {
                        val stages = resourceState.data
                        _state.update { current ->
                            current.copy(
                                status = UiStatus.SUCCESS,
                                stages = stages
                            )
                        }
                        viewModelScope.launch { successHandler?.let { successHandler() } }
                    }

                    is ResourceState.Error -> {
                        val error = resourceState.resId
                        _state.update { it.copy(status = UiStatus.ERROR, error = error.toString()) }
                    }
                }
            }
        }
    }

    fun getForms(projectId: String, successHandler: (() -> Unit)? = null){
        _state.update { it.copy(status = UiStatus.LOADING) }

        viewModelScope.launch (Dispatchers.IO) {
            repository.getAllForm(projectId).collectLatest { resourceState ->
                when(resourceState) {
                    is ResourceState.Success -> {
                        val forms = resourceState.data
                        _state.update { current ->
                            current.copy(
                                status = UiStatus.SUCCESS,
                                forms = forms
                            )
                        }
                        viewModelScope.launch { successHandler?.let { successHandler() } }
                    }

                    is ResourceState.Error -> {
                        val error = resourceState.resId
                        _state.update { it.copy(status = UiStatus.ERROR, error = error.toString()) }
                    }
                }

            }
        }
    }




    companion object {
        const val TAG = "DetailViewModel"
    }
}
