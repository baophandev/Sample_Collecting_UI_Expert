package com.application.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.constant.UiStatus
import com.application.data.repository.ProjectRepository
import com.application.ui.state.HomeUiState
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
class HomeViewModel @Inject constructor(
    private val repository: ProjectRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state = _state.asStateFlow()

    fun loadNewProject(projectId: String) {
        _state.update { it.copy(status = UiStatus.LOADING) }

        viewModelScope.launch(Dispatchers.IO) {
            repository.getProject(projectId).collectLatest { resourceState ->
                when (resourceState) {
                    is ResourceState.Success -> {
                        val project = resourceState.data
                        _state.update { current ->
                            val projects = current.projects.toMutableList()
                            projects.add(0, project)
                            current.copy(
                                status = UiStatus.SUCCESS,
                                projects = projects
                            )
                        }
                    }

                    is ResourceState.Error -> {
                        val error = resourceState.resId
                        _state.update { it.copy(status = UiStatus.ERROR, error = error) }
                    }
                }
            }
        }
    }

    fun getProjects(userId: String, successHandler: (() -> Unit)? = null) {
        _state.update { it.copy(status = UiStatus.LOADING) }

        viewModelScope.launch(Dispatchers.IO) {
            repository.getAllProject(userId).collectLatest { resourceState ->
                when (resourceState) {
                    is ResourceState.Success -> {
                        val projects = resourceState.data
                        _state.update { current ->
                            current.copy(
                                status = UiStatus.SUCCESS,
                                projects = projects
                            )
                        }
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

}