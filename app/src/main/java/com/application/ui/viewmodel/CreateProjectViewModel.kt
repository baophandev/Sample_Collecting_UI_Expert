package com.application.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.R
import com.application.data.repository.ProjectRepository
import com.application.ui.state.CreateProjectUiState
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
class CreateProjectViewModel @Inject constructor(
    private val projectRepository: ProjectRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CreateProjectUiState())
    val state = _state.asStateFlow()

    fun updateThumbnail(thumbnail: Uri) {
        _state.update { it.copy(thumbnail = thumbnail) }
    }

    fun updateTitle(title: String) {
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

    fun addMemberEmail(memberEmail: String) {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.getUserByEmail(email = memberEmail)
                .onStart { _state.update { it.copy(loading = true) } }
                .collectLatest { resourceState ->
                    when (resourceState) {
                        is ResourceState.Success -> {
                            val user = resourceState.data as? User
                            if (user != null) {
                                val newMemberId = user.id
                                _state.update {
                                    val updatedEmails = it.memberEmailMap.toMutableMap().apply {
                                        this[memberEmail] = newMemberId // email -> ID
                                    }
                                    it.copy(
                                        loading = false,
                                        memberIds = updatedEmails.values.toList(),
                                        memberEmailMap = updatedEmails
                                    )
                                }
                            } else {
                                _state.update {
                                    it.copy(loading = false, error = R.string.error_invalid_data)
                                }
                            }

                        }

                        is ResourceState.Error -> _state.update {
                            it.copy(loading = false, error = resourceState.resId)
                        }
                    }
                }
        }
    }

    fun removeMemberEmail(index: Int) {
        val currentState = _state.value

        val currentEmails = currentState.memberEmailMap.keys.toList()
        if (index in currentEmails.indices) {
            val emailToRemove = currentEmails[index]
            val updatedEmails = currentState.memberEmailMap.toMutableMap().apply {
                remove(emailToRemove)
            }
            _state.update {
                it.copy(
                    memberIds = updatedEmails.values.toList(),
                    memberEmailMap = updatedEmails
                )
            }
        }
    }

    fun gotError() {
        _state.update { it.copy(error = null) }
    }

    fun submit(successHandler: (String) -> Unit) {
        if (!validateFields()) return

        _state.update { it.copy(loading = true) }

        val currentState = state.value
        val thumbnail = currentState.thumbnail
        val collectAction: (ResourceState<String>) -> Unit = { resourceState ->
            when (resourceState) {
                is ResourceState.Error -> _state.update {
                    it.copy(loading = false, error = resourceState.resId)
                }

                is ResourceState.Success -> {
                    _state.update { it.copy(loading = false) }
                    viewModelScope.launch {
                        successHandler(resourceState.data)
                    }
                }
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            projectRepository.createProject(
                thumbnail = thumbnail,
                name = currentState.name,
                description = currentState.description,
                startDate = currentState.startDate,
                endDate = currentState.endDate,
                memberIds = currentState.memberIds,
            ).collectLatest(collectAction)
        }
    }

    private fun validateFields(): Boolean {
        val currentState = state.value
        if (currentState.name.isBlank()) {
            _state.update { it.copy(error = R.string.error_empty_project_name) }
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