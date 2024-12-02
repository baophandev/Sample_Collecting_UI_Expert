package com.application.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.constant.UiStatus
import com.application.data.repository.FormRepository
import com.application.data.repository.ProjectRepository
import com.application.data.repository.StageRepository
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
    private val projectRepository: ProjectRepository,
    private val stageRepository: StageRepository,
    private val formRepository: FormRepository,
) : ViewModel() {

    // luu vao day
    private val _state = MutableStateFlow(DetailUiState())
    val state = _state.asStateFlow()

    fun loadProject(
        projectId: String,
        skipCached: Boolean = false,
        onComplete: ((Boolean) -> Unit)? = null
    ) {
        _state.update { it.copy(status = UiStatus.LOADING) }
        viewModelScope.launch(Dispatchers.IO) {
            projectRepository.getProject(projectId, skipCached).collectLatest { resourceState ->
                when (resourceState) {
                    is ResourceState.Error -> _state.update { it.copy(status = UiStatus.ERROR) }
                    is ResourceState.Success -> _state.update {
                        it.copy(project = resourceState.data, status = UiStatus.SUCCESS)
                    }
                }

                onComplete?.let {
                    viewModelScope.launch { onComplete(resourceState is ResourceState.Success) }
                }
            }
        }
    }

    fun deleteProject(
        projectId: String,
        projectOwnerId:String? = null,
        successHandler: () -> Unit
    ) {
        if (projectOwnerId.isNullOrEmpty()){
            _state.update { it.copy(status = UiStatus.ERROR) }
            return
        }
        _state.update { it.copy(status = UiStatus.LOADING) }
        viewModelScope.launch(Dispatchers.IO) {
            projectRepository.deleteProject(projectId = projectId).collectLatest { resourceState ->
                when (resourceState) {
                    is ResourceState.Success -> {
                        viewModelScope.launch { successHandler() }
                    }

                    is ResourceState.Error -> {
                        val error = resourceState.resId
                        _state.update { it.copy(status = UiStatus.ERROR, error = error) }
                    }
                }
            }
        }
    }

    fun getStages(projectId: String, successHandler: ((Boolean) -> Unit)? = null) {
        _state.update { it.copy(stageStatus = UiStatus.LOADING) }
        viewModelScope.launch(Dispatchers.IO) {
            stageRepository.getAllStages(projectId).collectLatest { resourceState ->
                when (resourceState) {
                    is ResourceState.Success -> {
                        val stages = resourceState.data
                        _state.update { current ->
                            current.copy(
                                stageStatus = UiStatus.SUCCESS,
                                stages = stages
                            )
                        }
                        viewModelScope.launch { successHandler?.let { successHandler(true) } }
                    }

                    is ResourceState.Error -> {
                        val error = resourceState.resId
                        _state.update { currentState ->
                            currentState.copy(stageStatus = UiStatus.ERROR, error = error)
                        }
                    }
                }
            }
        }
    }

    fun getForms(projectId: String, successHandler: ((Boolean) -> Unit)? = null) {
        _state.update { it.copy(formStatus = UiStatus.LOADING) }

        viewModelScope.launch(Dispatchers.IO) {
            formRepository.getAllForms(projectId).collectLatest { resourceState ->
                when (resourceState) {
                    is ResourceState.Success -> {
                        val forms = resourceState.data
                        _state.update { current ->
                            current.copy(
                                formStatus = UiStatus.SUCCESS,
                                forms = forms
                            )
                        }
                        viewModelScope.launch { successHandler?.let { successHandler(true) } }
                    }

                    is ResourceState.Error -> {
                        val error = resourceState.resId
                        _state.update { it.copy(status = UiStatus.ERROR, error = error) }
                    }
                }

            }
        }
    }

    fun deleteForm(
        formId: String,
        //stageId:String,
        successHandler: (() -> Unit)? = null) {
        _state.update { it.copy(status = UiStatus.LOADING) }
        viewModelScope.launch(Dispatchers.IO) {

                formRepository.deleteForm(formId).collectLatest { resourceState ->
                    when (resourceState) {
                        is ResourceState.Success -> {
                            viewModelScope.launch { successHandler?.let { successHandler() } }
                        }

                        is ResourceState.Error -> {
                            val error = resourceState.resId
                            _state.update { it.copy(status = UiStatus.ERROR, error = error) }
                        }
                    }
                }
            }

    }


    companion object {
        const val TAG = "DetailViewModel"
    }
}
