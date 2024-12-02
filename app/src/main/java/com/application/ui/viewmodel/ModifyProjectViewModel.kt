package com.application.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.android.utility.state.ResourceState
import com.application.constant.UiStatus
import com.application.data.repository.ProjectRepository
import com.application.ui.state.ModifyProjectUiState
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
class ModifyProjectViewModel @Inject constructor(
    private val repository: ProjectRepository
) : ViewModel() {
    private val _state = MutableStateFlow(ModifyProjectUiState())
    val state = _state.asStateFlow()

    fun loadProject(projectId: String) {
        _state.update { it.copy(status = UiStatus.LOADING, isUpdated = false) }
        viewModelScope.launch(Dispatchers.IO) {
            repository.getProject(projectId).collectLatest { resourceState ->
                when (resourceState) {
                    is ResourceState.Success -> _state.update {
                        it.copy(status = UiStatus.SUCCESS, project = resourceState.data)
                    }

                    is ResourceState.Error -> _state.update {
                        it.copy(status = UiStatus.ERROR, error = resourceState.resId)
                    }
                }
            }
        }
    }

    fun updateThumbnail(thumbnail: Uri) {
        val currentProject = state.value.project
        _state.update {
            it.copy(
                project = currentProject?.copy(thumbnail = thumbnail),
                isUpdated = true
            )
        }
    }

    fun updateProjectName(name: String) {
        val currentProject = state.value.project
        _state.update { it.copy(project = currentProject?.copy(name = name), isUpdated = true) }
    }

    fun updateDescription(description: String) {
        val currentProject = state.value.project
        _state.update {
            it.copy(
                project = currentProject?.copy(description = description),
                isUpdated = true
            )
        }
    }

    fun updateDate(date: String, isStartDate: Boolean) {
        val currentProject = state.value.project
        if (isStartDate) _state.update {
            it.copy(
                project = currentProject?.copy(startDate = date),
                isUpdated = true
            )
        }
        else _state.update {
            it.copy(
                project = currentProject?.copy(endDate = date),
                isUpdated = true
            )
        }
    }

    fun submit(successHandler: (Boolean) -> Unit) {
        if (validate() || state.value.project == null || !state.value.isUpdated) return
        val currentProject = state.value.project!!

        viewModelScope.launch(Dispatchers.IO) {
            repository.updateProject(
                projectId = currentProject.id,
                thumbnail = currentProject.thumbnail,
                name = currentProject.name,
                description = currentProject.description,
                startDate = currentProject.startDate,
                endDate = currentProject.endDate
            )
                .onStart { _state.update { it.copy(status = UiStatus.LOADING) } }
                .collectLatest { resourceState ->
                    when (resourceState) {
                        is ResourceState.Error -> _state.update {
                            it.copy(status = UiStatus.ERROR, error = resourceState.resId)
                        }

                        is ResourceState.Success -> {
                            _state.update { it.copy(status = UiStatus.SUCCESS) }
                            viewModelScope.launch {
                                successHandler(resourceState.data)
                            }
                        }
                    }
                }
        }
    }

    private fun validate(): Boolean {
        val currentProject = state.value.project
        val startDate = currentProject?.startDate
        val endDate = currentProject?.endDate

        if (currentProject?.name == null ||
            (startDate != null && endDate != null && startDate > endDate)
        )
            return false
        return currentProject.name.isBlank()
    }
}