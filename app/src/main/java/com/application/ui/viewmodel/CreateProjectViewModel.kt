package com.application.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.R
import com.application.data.entity.Project
import com.application.data.entity.ProjectData
import com.application.data.repository.ProjectRepository
import com.application.ui.state.CreateProjectUiState
import com.application.util.ResourceState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class CreateProjectViewModel @Inject constructor(
    private val repository: ProjectRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CreateProjectUiState())
    val state = _state.asStateFlow()

    fun updateThumbnail(thumbnail: Pair<String, Uri>) {
        _state.update { it.copy(thumbnail = thumbnail) }
    }

    fun updateTitle(title: String) {
        _state.update { it.copy(title = title) }
    }

    fun updateDescription(description: String) {
        _state.update { it.copy(description = description) }
    }

    fun updateDate(date: Long, isStartDate: Boolean) {
        if (isStartDate) {
            _state.update { it.copy(startDate = Date(date)) }
        } else {
            _state.update { it.copy(endDate = Date(date)) }
        }
    }

    fun gotError() {
        _state.update { it.copy(error = null) }
    }

    fun submit(userId: String, successHandler: (Project) -> Unit) {
        if (!validateFields()) return

        _state.update { it.copy(loading = true) }

        val currentState = state.value
        val thumbnail = currentState.thumbnail
        val projectData = ProjectData(
            thumbnailPath = thumbnail?.first,
            title = currentState.title,
            description = currentState.description,
            startDate = currentState.startDate?.time,
            endDate = currentState.endDate?.time,
            emailOwner = userId
        )
        val collectAction: (ResourceState<String>) -> Unit = { resourceState ->
            when (resourceState) {
                is ResourceState.Error -> _state.update {
                    it.copy(loading = false, error = resourceState.error)
                }

                is ResourceState.Success -> {
                    _state.update { it.copy(loading = false) }
                    viewModelScope.launch {
                        successHandler(
                            Project(
                                projectId = resourceState.data,
                                data = projectData
                            )
                        )
                    }
                }
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            repository.createProject(
                name = currentState.title,
                description = currentState.description,
                startDate = currentState.startDate,
                endDate = currentState.endDate,
                memberIds = currentState.memberIds,
                ownerId = userId,
            ).collectLatest(collectAction)
        }
    }

    private fun validateFields(): Boolean {
        val currentState = state.value
        return if (currentState.title.isBlank()
            || currentState.startDate == null
            || currentState.endDate == null
        ) {
            _state.update { it.copy(error = R.string.fields_not_validate) }
            false
        } else if (currentState.startDate > currentState.endDate) {
            _state.update { it.copy(error = R.string.start_date_greater_than_end_date) }
            false
        } else true
    }
}