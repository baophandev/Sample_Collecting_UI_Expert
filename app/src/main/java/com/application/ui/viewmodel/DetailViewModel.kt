package com.application.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.android.user_library.repository.UserRepository
import com.application.android.utility.state.ResourceState
import com.application.constant.UiStatus
import com.application.data.repository.FormRepository
import com.application.data.repository.ProjectRepository
import com.application.data.repository.StageRepository
import com.application.ui.state.DetailUiState
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
class DetailViewModel @Inject constructor(
    private val projectRepository: ProjectRepository,
    private val stageRepository: StageRepository,
    private val formRepository: FormRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    // luu vao day
    private val _state = MutableStateFlow(DetailUiState())
    val state = _state.asStateFlow()

    fun isProjectOwner(): Boolean {
        val ownerId = state.value.project?.owner?.id
        val currentLoggedUserId = userRepository.loggedUser?.id

        return ownerId?.equals(currentLoggedUserId) == true
    }

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
        projectOwnerId: String? = null,
        successHandler: () -> Unit
    ) {
        if (projectOwnerId.isNullOrEmpty()) {
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

    fun deleteForm(formId: String) {
        _state.update { it.copy(status = UiStatus.LOADING) }
        viewModelScope.launch(Dispatchers.IO) {
            formRepository.deleteForm(formId).collectLatest { resourceState ->
                when (resourceState) {
                    is ResourceState.Success -> {
                        val currentForms = state.value.forms.toMutableList()
                        currentForms.removeIf { it.id == formId }
                        _state.update { it.copy(status = UiStatus.SUCCESS, forms = currentForms) }
                    }

                    is ResourceState.Error -> {
                        val error = resourceState.resId
                        _state.update { it.copy(status = UiStatus.ERROR, error = error) }
                    }
                }
            }
        }

    }

    fun isFormUsed(formId: String): Boolean {
        // Kiểm tra xem stage nào có sử dụng formId này
        return state.value.stages.any { it.formId == formId }
    }

    fun updateProjectInHome(successHandler: (Boolean) -> Unit) {
        val currentProject = state.value.project!!
        val projectId = currentProject.id

        viewModelScope.launch(Dispatchers.IO) {
            projectRepository.getProject(projectId)
                .onStart { _state.update { it.copy(status = UiStatus.LOADING) } }
                .collectLatest { resourceState ->
                    when (resourceState) {
                        is ResourceState.Error -> _state.update {
                            it.copy(status = UiStatus.ERROR, error = resourceState.resId)
                        }

                        is ResourceState.Success -> {
                            _state.update { it.copy(status = UiStatus.SUCCESS) }
                            viewModelScope.launch {
                                successHandler(true)
                            }
                        }
                    }
                }
        }
    }

    companion object {
        const val TAG = "DetailViewModel"
    }
}
