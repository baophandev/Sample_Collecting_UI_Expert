package com.application.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.data.entity.Project
import com.application.ui.state.ModifyProjectUiState
import com.application.util.ResourceState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ModifyProjectViewModel @Inject constructor(
) : ViewModel() {
    private val _state = MutableStateFlow(ModifyProjectUiState(init = true))
    val state = _state.asStateFlow()

    fun setModifiedProject(project: Project, thumbnailUri: Uri? = null) {
//        project.data.memberIds?.values?.let { memberIds ->
//            val currentMemberIds = state.value.memberIds.toMutableList()
//            currentMemberIds.addAll(memberIds)
//            _state.update { it.copy(memberIds = currentMemberIds.toList()) }
//        }
//
//        _state.update {
//            it.copy(
//                init = false,
//                title = project.data.title ?: "",
//                description = project.data.description ?: "",
//                startDate = project.data.startDate,
//                endDate = project.data.endDate,
//                thumbnailPath = if (thumbnailUri != null) {
//                    Pair(project.data.thumbnailPath!!, thumbnailUri)
//                } else null
//            )
//        }
    }

    fun updateThumbnail(thumbnail: Pair<String, Uri>) {
        _state.update { it.copy(thumbnailPath = thumbnail) }
    }

    fun updateTitle(title: String) {
        _state.update { it.copy(title = title) }
    }

    fun updateDescription(description: String) {
        _state.update { it.copy(description = description) }
    }

    fun updateDate(date: Long, isStartDate: Boolean) {
        if (isStartDate) _state.update { it.copy(startDate = date) }
        else _state.update { it.copy(endDate = date) }
    }

    fun submit(preProject: Project, successHandler: () -> Unit) {
        if (!validate()) return

        val currentState = state.value
        val preData = preProject
//        val modifiedThumbnail =
//            if (currentState.thumbnailPath != null && currentState.thumbnailPath.first != preProject.data.thumbnailPath)
//                currentState.thumbnailPath else null

        val modifiedTitle = if (currentState.title != preData.name) currentState.title else null
        val modifiedDescription =
            if (currentState.description != preData.description) currentState.description else null
        val modifiedStartDate =
            if (currentState.startDate != preData.startDate) currentState.startDate else null
        val modifiedEndDate =
            if (currentState.endDate != preData.endDate) currentState.endDate else null
        val modifiedMemberEmailList = state.value.memberIds.toList()

        val collectAction: (ResourceState<Boolean>) -> Unit = { resourceState ->
            when (resourceState) {
                is ResourceState.Success -> {
                    _state.update { it.copy(loading = false) }
                    viewModelScope.launch { successHandler() }
                }

                is ResourceState.Error -> _state.update {
                    it.copy(loading = false, error = resourceState.error)
                }

                else -> {}
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
        }
    }

    private fun validate(): Boolean {
        val currentState = state.value
        return !((currentState.startDate != null &&
                currentState.endDate != null &&
                currentState.startDate > currentState.endDate) ||
                currentState.title.isBlank())
    }
}