package com.application.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.constant.UiStatus
import com.application.data.entity.Project
import com.application.data.entity.User
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

    init {
        val mockData = listOf(
            Project(
                id = "test-project-id",
                thumbnailUri = null,
                name = "Project Test",
                description = "Test project",
                owner = User(
                    id = "",
                    username = "",
                    name = ""
                ),
                endDate = System.currentTimeMillis(),
                startDate = System.currentTimeMillis(),
                forms = listOf(),
                stages = listOf()
            )
        )
        _state.update { it.copy(projects = mockData) }
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
                        val error = resourceState.error
                        _state.update { it.copy(status = UiStatus.ERROR, error = error) }
                    }
                }
            }
        }
    }

    companion object {
        const val TAG = "HomeViewModel"
    }
}