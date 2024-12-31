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
import io.github.nhatbangle.sc.user.entity.User
import io.github.nhatbangle.sc.user.repository.UserRepository
import io.github.nhatbangle.sc.utility.state.ResourceState
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.nhatbangle.sc.utility.validate.RegexValidation
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.last
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
        if (!RegexValidation.EMAIL.matches(memberEmail)) {
            _state.update { it.copy(error = R.string.error_invalid_email) }
            return
        }

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
        val currentState = state.value
        if (currentState.project == null) return

        val currentMembers = currentState.projectUsers
        currentMembers.getOrNull(index)?.let { member ->
            viewModelScope.launch(Dispatchers.IO) {
                _state.update { it.copy(status = UiStatus.LOADING) }
                when (val rsState = repository.checkMemberInAnyStage(
                    projectId = currentState.project.id,
                    userId = member.id
                ).last()) {
                    is ResourceState.Error -> _state.update {
                        it.copy(
                            status = UiStatus.SUCCESS,
                            error = R.string.unknown_error
                        )
                    }

                    is ResourceState.Success -> {
                        _state.update {
                            if (!rsState.data) {
                                val mutableMembers = currentMembers.toMutableList()
                                mutableMembers.removeAt(index)

                                val deletedMemberIds = state.value.deletedMemberIds.toMutableList()
                                deletedMemberIds.add(member.id)
                                it.copy(
                                    status = UiStatus.SUCCESS,
                                    projectUsers = mutableMembers,
                                    deletedMemberIds = deletedMemberIds,
                                    isUpdated = true
                                )
                            } else it.copy(
                                status = UiStatus.SUCCESS,
                                error = R.string.error_member_in_stage
                            )
                        }
                    }
                }
            }
        }
    }

    private suspend fun updateProjectToRepository(updatedProject: Project): Boolean {
        val resourceState = repository.updateProject(
            projectId = updatedProject.id,
            thumbnail = if (state.value.isThumbnailUpdated) updatedProject.thumbnail else null,
            name = updatedProject.name,
            description = updatedProject.description,
            startDate = updatedProject.startDate,
            endDate = updatedProject.endDate
        ).last()
        return when (resourceState) {
            is ResourceState.Error -> false
            is ResourceState.Success -> true
        }
    }

    private suspend fun addMemberToRepository(
        projectId: String,
        addedMembers: List<User>
    ): List<Deferred<Boolean>> = coroutineScope {
        addedMembers.map { addedMember ->
            async {
                val resourceState = repository.updateProjectMember(
                    projectId = projectId,
                    memberId = addedMember.id,
                    operator = MemberOperator.ADD
                ).last()
                when (resourceState) {
                    is ResourceState.Error -> false
                    is ResourceState.Success -> true
                }
            }
        }
    }

    private suspend fun deleteMemberToRepository(
        projectId: String,
        deleteMemberIds: List<String>,
    ): List<Deferred<Boolean>> = coroutineScope {
        deleteMemberIds.map { memberId ->
            async {
                val resourceState = repository.updateProjectMember(
                    projectId = projectId,
                    memberId = memberId,
                    operator = MemberOperator.REMOVE
                ).last()
                when (resourceState) {
                    is ResourceState.Error -> false
                    is ResourceState.Success -> true
                }
            }
        }
    }

    fun submit(successHandler: (Boolean) -> Unit) {
        val currentState = state.value
        if (!validate() || currentState.project == null || !currentState.isUpdated) return

        val currentProject = currentState.project
        val currentMembers = currentState.projectUsers

        _state.update { it.copy(status = UiStatus.LOADING) }
        viewModelScope.launch(Dispatchers.IO) {
            val results = mutableListOf<Boolean>()

            results += updateProjectToRepository(
                updatedProject = currentProject,
            )

            if (currentState.addedMemberIds.isNotEmpty()) {
                val addedMemberIds = currentState.addedMemberIds
                val addedMembers = currentMembers.filter { addedMemberIds.contains(it.id) }
                results += addMemberToRepository(
                    projectId = currentProject.id,
                    addedMembers = addedMembers,
                ).awaitAll()
            }
            if (currentState.deletedMemberIds.isNotEmpty()) {
                val deletedMemberIds = currentState.deletedMemberIds
                results += deleteMemberToRepository(
                    projectId = currentProject.id,
                    deleteMemberIds = deletedMemberIds,
                ).awaitAll()
            }

            viewModelScope.launch {
                val finalResult = results.all { it }
                if (finalResult) _state.update { ModifyProjectUiState() }
                successHandler(finalResult)
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