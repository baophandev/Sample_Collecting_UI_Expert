package com.application.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.R
import com.application.constant.MemberOperator
import com.application.constant.UiStatus
import com.application.data.entity.Project
import com.application.data.repository.ProjectRepository
import com.application.ui.state.ModifyProjectUiState
import com.sc.library.user.entity.User
import com.sc.library.user.repository.UserRepository
import com.sc.library.utility.state.ResourceState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ModifyProjectViewModel @Inject constructor(
    private val repository: ProjectRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _state = MutableStateFlow(ModifyProjectUiState())
    val state = _state.asStateFlow()

    fun loadProject(projectId: String) {
        _state.update { it.copy(status = UiStatus.LOADING, isUpdated = false) }
        viewModelScope.launch(Dispatchers.IO) {
            repository.getProject(projectId).collectLatest { resourceState ->
                when (resourceState) {
                    is ResourceState.Success -> _state.update {
                        it.copy(
                            status = UiStatus.SUCCESS,
                            project = resourceState.data,
                            projectUsers = resourceState.data.members
                        )
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
                isUpdated = true,
                isThumbnailUpdated = true
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

    fun addNewProjectMember(memberEmail: String) {
        val currentState = state.value
        val existUser = currentState.projectUsers.find { it.email == memberEmail }
        if (existUser != null) {
            _state.update { it.copy(error = R.string.error_is_a_member_of_project) }
            return
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                userRepository.getUserByEmail(email = memberEmail)
                    .collectLatest { resourceState ->
                        when (resourceState) {
                            is ResourceState.Success -> {
                                val user = resourceState.data as? User
                                if (user != null) {
                                    _state.update {
                                        it.copy(
                                            projectUsers = it.projectUsers + user,
                                            addedMemberIds = it.addedMemberIds + user.id,
                                            isUpdated = true
                                        )
                                    }
                                } else {
                                    _state.update {
                                        it.copy(error = R.string.error_cannot_get_user_by_email)
                                    }
                                }

                            }

                            is ResourceState.Error -> _state.update {
                                it.copy(error = resourceState.resId)
                            }
                        }
                    }
            }
        }
    }

    fun removeMemberEmail(index: Int) {
        val currentMembers = _state.value.projectUsers
        currentMembers.getOrNull(index)?.let { member ->
            val mutableMembers = currentMembers.toMutableList()
            mutableMembers.removeAt(index)

            val deletedMemberIds = state.value.deletedMemberIds.toMutableList()
            deletedMemberIds.add(member.id)
            _state.update {
                it.copy(
                    projectUsers = mutableMembers,
                    deletedMemberIds = deletedMemberIds,
                    isUpdated = true
                )
            }

        }
    }

    private suspend fun updateProjectToRepository(
        updatedProject: Project,
        successHandler: (Boolean) -> Unit
    ) {
        repository.updateProject(
            projectId = updatedProject.id,
            thumbnail = if (state.value.isThumbnailUpdated) updatedProject.thumbnail else null,
            name = updatedProject.name,
            description = updatedProject.description,
            startDate = updatedProject.startDate,
            endDate = updatedProject.endDate
        ).collectLatest { resourceState ->
            when (resourceState) {
                is ResourceState.Error -> _state.update {
                    it.copy(
                        status = UiStatus.SUCCESS,
                        error = resourceState.resId
                    )
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

    private suspend fun addMemberToRepository(
        projectId: String,
        addedMembers: List<User>,
        successHandler: (Boolean) -> Unit
    ) {
        addedMembers.forEach { addedMember ->
            repository.updateProjectMember(
                projectId = projectId,
                memberId = addedMember.id,
                operator = MemberOperator.ADD
            ).collectLatest { resourceState ->
                when (resourceState) {
                    is ResourceState.Error -> _state.update {
                        it.copy(
                            status = UiStatus.SUCCESS,
                            error = resourceState.resId
                        )
                    }

                    is ResourceState.Success -> {
                        _state.update {
                            it.copy(
                                status = UiStatus.SUCCESS,
                                projectUsers = it.projectUsers + addedMember // Thêm user mới
                            )
                        }
                        successHandler(resourceState.data)
                    }
                }
            }
        }
    }

    private suspend fun deleteMemberToRepository(
        projectId: String,
        deleteMemberIds: List<String>,
        successHandler: (Boolean) -> Unit
    ) {
        deleteMemberIds.forEach { memberId ->
            repository.updateProjectMember(
                projectId = projectId,
                memberId = memberId,
                operator = MemberOperator.REMOVE
            ).collectLatest { resourceState ->
                when (resourceState) {
                    is ResourceState.Error -> _state.update {
                        it.copy(
                            status = UiStatus.SUCCESS,
                            error = resourceState.resId
                        )
                    }

                    is ResourceState.Success -> {
                        _state.update {
                            val updatedUsers = it.projectUsers.filter { user ->
                                user.id != memberId // filter ra cac user co id khac voi id trong deleteMemberIds
                            }
                            it.copy(
                                status = UiStatus.SUCCESS,
                                projectUsers = updatedUsers
                            )
                        }
                        successHandler(resourceState.data)
                    }
                }
            }
        }
    }

    fun submit(successHandler: (Boolean) -> Unit) {
        if (!validate() || state.value.project == null || !state.value.isUpdated) return
        val currentState = state.value

        val currentProject = currentState.project
        val currentMembers = currentState.projectUsers

        _state.update { it.copy(status = UiStatus.LOADING) }
        if (currentProject != null) {
            viewModelScope.launch(Dispatchers.IO) {
                if (currentState.isUpdated) {
                    updateProjectToRepository(
                        updatedProject = currentProject,
                        successHandler = successHandler
                    )
                }
                if (currentState.addedMemberIds.isNotEmpty()) {
                    val addedMemberIds = currentState.addedMemberIds
                    val addedMembers = currentMembers.filter { addedMemberIds.contains(it.id) }
                    addMemberToRepository(
                        projectId = currentProject.id,
                        addedMembers = addedMembers,
                        successHandler = successHandler
                    )
                }
                if (currentState.deletedMemberIds.isNotEmpty()) {
                    val deletedMemberIds = currentState.deletedMemberIds
                    deleteMemberToRepository(
                        projectId = currentProject.id,
                        deleteMemberIds = deletedMemberIds,
                        successHandler = successHandler
                    )
                }
            }
        }
    }

    fun gotError() {
        _state.update { it.copy(error = null) }
    }

    private fun validate(): Boolean {
        val currentProject = state.value.project
        val startDate = currentProject?.startDate
        val endDate = currentProject?.endDate

        if (currentProject?.name?.isBlank() == true) {
            _state.update { it.copy(error = R.string.error_empty_project_name) }
            return false
        } else if (startDate == null || endDate == null) {
            _state.update { it.copy(error = R.string.error_empty_startDate_endDate) }
            return false
        } else if (startDate > endDate) {
            _state.update { it.copy(error = R.string.error_start_date_greater_than_end_date) }
            return false
        } else return true
    }
}