package com.application.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.application.R
import com.application.constant.UiStatus
import com.application.data.entity.Form
import com.application.data.paging.FormPagingSource
import com.application.data.repository.FormRepository
import com.application.data.repository.ProjectRepository
import com.application.data.repository.StageRepository
import com.application.ui.state.CreateStageUiState
import com.application.ui.viewmodel.DetailViewModel.Companion.TAG
import com.sc.library.utility.state.ResourceState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateStageViewModel @Inject constructor(
    private val stageRepository: StageRepository,
    private val formRepository: FormRepository,
    private val projectRepository: ProjectRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(CreateStageUiState())
    val state = _state.asStateFlow()

    lateinit var formFlow: Flow<PagingData<Form>>

    fun fetchForms(projectId: String) {
        formFlow = Pager(
            PagingConfig(
                pageSize = 3,
                enablePlaceholders = false,
                prefetchDistance = 1,
                initialLoadSize = 3,
            )
        ) {
            FormPagingSource(
                projectId = projectId,
                formRepository = formRepository
            )
        }.flow
            .cachedIn(viewModelScope)
            .catch { Log.e(TAG, it.message, it) }

        _state.update { it.copy(status = UiStatus.SUCCESS) }
    }

    fun fetchProjectMembers(projectId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            projectRepository.getProject(projectId).collectLatest { rsState ->
                when (rsState) {
                    is ResourceState.Success -> {
                        val project = rsState.data
                        _state.update { it.copy(projectMembers = project.members) }
                    }

                    is ResourceState.Error -> _state.update { it.copy(status = UiStatus.ERROR) }
                }
            }
        }
    }

    fun updateName(title: String) {
        _state.update { it.copy(name = title) }
    }

    fun updateDescription(description: String) {
        _state.update { it.copy(description = description) }
    }

    fun updateDate(date: String, isStartDate: Boolean) {
        if (isStartDate) {
            _state.update { it.copy(startDate = date) }
        } else {
            _state.update { it.copy(endDate = date) }
        }
    }

    fun addStageMemberEmail(memberEmail: String) {
        val currentState = state.value
        val existUser = currentState.projectMembers.find { it.email == memberEmail }
        if (existUser == null) {
            _state.update { it.copy(error = R.string.error_not_a_member_of_project) }
            return
        }

        val currentSelectedUsers = currentState.selectedUsers.toMutableList()
        currentSelectedUsers.add(existUser)
        _state.update { it.copy(selectedUsers = currentSelectedUsers) }
    }

    fun removeMemberEmail(index: Int) {
        val currentSelectedUsers = state.value.selectedUsers.toMutableList()
        currentSelectedUsers.removeAt(index)
        _state.update { it.copy(selectedUsers = currentSelectedUsers) }
    }

    fun selectForm(form: Form) {
        _state.update { it.copy(selectedForm = form) }
    }

    fun submit(successHandler: () -> Unit) {
        if (!validateFields()) return
        val currentState = state.value

        val collectAction: (ResourceState<String>) -> Unit = { resourceState ->
            when (resourceState) {
                is ResourceState.Error -> _state.update {
                    it.copy(status = UiStatus.SUCCESS, error = resourceState.resId)
                }

                is ResourceState.Success -> {
                    _state.update { CreateStageUiState() }
                    viewModelScope.launch { successHandler() }
                }
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            stageRepository.createStage(
                name = currentState.name,
                description = currentState.description,
                startDate = currentState.startDate,
                endDate = currentState.endDate,
                form = currentState.selectedForm!!,
            ).collectLatest(collectAction)
        }
    }

    fun gotError() {
        _state.update { it.copy(error = null) }
    }

    private fun validateFields(): Boolean {
        val currentState = state.value
        if (currentState.name.isBlank()) {
            _state.update { it.copy(error = R.string.error_empty_stage_name) }
            return false
        } else if (currentState.startDate == null || currentState.endDate == null) {
            _state.update { it.copy(error = R.string.error_empty_startDate_endDate) }
            return false
        } else if (currentState.startDate > currentState.endDate) {
            _state.update { it.copy(error = R.string.error_start_date_greater_than_end_date) }
            return false
        } else if (currentState.selectedForm == null) {
            _state.update { it.copy(error = R.string.form_not_selected) }
            return false
        }
        else return true
    }
}