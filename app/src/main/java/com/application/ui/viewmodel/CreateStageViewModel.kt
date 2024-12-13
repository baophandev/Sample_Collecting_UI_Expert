package com.application.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.R
import com.application.constant.UiStatus
import com.application.data.repository.FormRepository
import com.application.data.repository.ProjectRepository
import com.application.data.repository.StageRepository
import com.application.ui.state.CreateStageUiState
import com.sc.library.user.entity.User
import com.sc.library.user.repository.UserRepository
import com.sc.library.utility.state.ResourceState
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
class CreateStageViewModel @Inject constructor(
    private val stageRepository: StageRepository,
    private val formRepository: FormRepository,
    private val projectRepository: ProjectRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CreateStageUiState())
    val state = _state.asStateFlow()

    fun initialize(projectId: String) {
        getAllForms(projectId)
        getProjectMember(projectId)
    }

    private fun getAllForms(projectId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            formRepository.getAllForms(projectId).collectLatest { resourceState ->
                when (resourceState) {
                    is ResourceState.Success -> _state.update {
                        it.copy(
                            status = UiStatus.SUCCESS,
                            forms = resourceState.data
                        )
                    }

                    is ResourceState.Error -> _state.update { it.copy(status = UiStatus.ERROR) }
                }
            }
        }
    }

    private fun getProjectMember(projectId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            projectRepository.getProject(projectId).collectLatest { resourceState ->
                when (resourceState) {
                    is ResourceState.Success -> {
                        val project = resourceState.data
                        if (project.memberUsernames != null) {
                            _state.update {
                                it.copy(
                                    projectMemberEmails = project.memberUsernames
                                )
                            }
                        } else {
                            _state.update { it.copy(error = R.string.error_cannot_get_member_of_project) }
                        }
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

        if (!currentState.projectMemberEmails.contains(memberEmail)){
            _state.update { it.copy(error = R.string.error_not_a_member_of_project) }
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.getUserByEmail(email = memberEmail)
                .onStart { _state.update { it.copy(status = UiStatus.LOADING) } }
                .collectLatest { resourceState ->
                    when (resourceState) {
                        is ResourceState.Success -> {
                            val user = resourceState.data as? User
                            if (user != null) {
                                val newStageMemberId = user.id
                                _state.update {
                                    val updatedStageEmails = it.stageMemberEmailMap.toMutableMap().apply {
                                        this[memberEmail] = newStageMemberId // email -> ID
                                    }
                                    it.copy(
                                        status = UiStatus.SUCCESS,
                                        stageMemberEmails = updatedStageEmails.values.toList(),
                                        stageMemberEmailMap = updatedStageEmails
                                    )
                                }
                            } else {
                                _state.update {
                                    it.copy(status = UiStatus.SUCCESS, error = R.string.error_cannot_get_user_by_email)
                                }
                            }

                        }

                        is ResourceState.Error -> _state.update {
                            it.copy(status = UiStatus.ERROR, error = resourceState.resId)
                        }
                    }
                }
        }
    }

    fun removeMemberEmail(index: Int) {
        val currentState = _state.value

        val currentEmails = currentState.stageMemberEmailMap.keys.toList()
        if (index in currentEmails.indices) {
            val emailToRemove = currentEmails[index]
            val updatedEmails = currentState.stageMemberEmailMap.toMutableMap().apply {
                remove(emailToRemove)
            }
            _state.update {
                it.copy(
                    stageMemberEmails = updatedEmails.values.toList(),
                    stageMemberEmailMap = updatedEmails
                )
            }
        }
    }

    fun selectForm(formIdx: Int) {
        val form = state.value.forms[formIdx]
        _state.update { it.copy(selectedForm = Pair(form.id, form.title)) }
    }

    fun submitStage(projectId: String, formId: String, successHandler: (Boolean) -> Unit) {
        if (!validateFields()) return
        val currentState = state.value

        val collectAction: (ResourceState<String>) -> Unit = { resourceState ->
            when (resourceState) {
                is ResourceState.Error -> _state.update {
                    it.copy(status = UiStatus.SUCCESS, error = resourceState.resId)
                }

                is ResourceState.Success -> {
                    _state.update { it.copy(status = UiStatus.SUCCESS) }
                    viewModelScope.launch { successHandler(true) }
                }
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            stageRepository.createStage(
                name = currentState.name,
                description = currentState.description,
                startDate = currentState.startDate,
                endDate = currentState.endDate,
                formId = formId,
                projectOwnerId = projectId
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
        } else return true
    }
}