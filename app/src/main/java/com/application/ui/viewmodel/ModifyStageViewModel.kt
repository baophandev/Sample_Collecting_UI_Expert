package com.application.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.application.R
import com.application.constant.MemberOperator
import com.application.constant.UiStatus
import com.application.data.entity.Form
import com.application.data.entity.Stage
import com.application.data.paging.FormPagingSource
import com.application.data.repository.FormRepository
import com.application.data.repository.ProjectRepository
import com.application.data.repository.StageRepository
import com.application.ui.state.ModifyStageUiState
import com.application.ui.viewmodel.HomeViewModel.Companion.TAG
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ModifyStageViewModel @Inject constructor(
    private val stageRepository: StageRepository,
    private val formRepository: FormRepository,
    private val userRepository: UserRepository,
    private val projectRepository: ProjectRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(ModifyStageUiState())
    val state = _state.asStateFlow()

    lateinit var flow: Flow<PagingData<Form>>

    fun fetchStage(projectId: String, stageId: String) {
        _state.update { it.copy(status = UiStatus.LOADING, isUpdated = false) }

        viewModelScope.launch(Dispatchers.IO) {
            stageRepository.getStage(stageId).collectLatest { rsState ->
                when (rsState) {
                    is ResourceState.Success -> {
                        val stage = rsState.data
                        when (val formRsState = formRepository.getForm(stage.formId).last()) {
                            is ResourceState.Error -> _state.update {
                                it.copy(
                                    status = UiStatus.ERROR,
                                    error = formRsState.resId
                                )
                            }

                            is ResourceState.Success -> _state.update {
                                it.copy(
                                    status = UiStatus.SUCCESS,
                                    selectedForm = formRsState.data,
                                    stage = stage,
                                    stageUsers = stage.members
                                )
                            }
                        }
                    }

                    is ResourceState.Error -> _state.update {
                        it.copy(status = UiStatus.ERROR, error = rsState.resId)
                    }
                }
            }
        }

        flow = Pager(
            PagingConfig(
                pageSize = 3,
                enablePlaceholders = false,
                prefetchDistance = 1,
                initialLoadSize = 3,
            )
        ) {
            FormPagingSource(projectId = projectId, formRepository = formRepository)
        }.flow
            .cachedIn(viewModelScope)
            .catch { Log.e(TAG, it.message, it) }
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

    fun updateTitle(name: String) {
        val currentStage = state.value.stage
        _state.update { it.copy(stage = currentStage?.copy(name = name), isUpdated = true) }
    }

    fun updateDescription(description: String) {
        val currentStage = state.value.stage
        _state.update {
            it.copy(
                stage = currentStage?.copy(description = description),
                isUpdated = true
            )
        }
    }

    fun updateDate(date: String, isStartDate: Boolean) {
        val currentStage = state.value.stage
        if (isStartDate) _state.update {
            it.copy(
                stage = currentStage?.copy(startDate = date),
                isUpdated = true
            )
        }
        else _state.update {
            it.copy(
                stage = currentStage?.copy(endDate = date),
                isUpdated = true
            )
        }
    }

    fun updateFormId(form: Form) {
        _state.update { it.copy(selectedForm = form, isUpdated = true) }
    }

    fun addNewStageMember(memberEmail: String) {
        if (!RegexValidation.EMAIL.matches(memberEmail)) {
            _state.update { it.copy(error = R.string.error_invalid_email) }
            return
        }

        val currentState = state.value

        if (memberEmail !in currentState.projectMembers.map { it.email }) {
            _state.update { it.copy(error = R.string.error_not_a_member_of_project) }
            return
        }


        val existUser = currentState.stageUsers.find { it.email == memberEmail }
        if (existUser != null) {
            _state.update { it.copy(error = R.string.error_is_a_member_of_stage) }
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            userRepository.getUserByEmail(email = memberEmail)
                .collectLatest { resourceState ->
                    when (resourceState) {
                        is ResourceState.Success -> {
                            val user = resourceState.data as? User
                            if (user != null) {
                                _state.update {
                                    it.copy(
                                        stageUsers = it.stageUsers + user,
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

    fun removeMemberEmail(index: Int) {
        val currentMembers = _state.value.stageUsers
        currentMembers.getOrNull(index)?.let { member ->
            val mutableMembers = currentMembers.toMutableList()
            mutableMembers.removeAt(index)

            val deletedMemberIds = state.value.deletedMemberIds.toMutableList()
            deletedMemberIds.add(member.id)
            _state.update {
                it.copy(
                    stageUsers = mutableMembers,
                    deletedMemberIds = deletedMemberIds,
                    isUpdated = true
                )
            }

        }
    }

    private suspend fun updateStageToRepository(updatedStage: Stage): Boolean {
        val resourceState = stageRepository.updateStage(
            stageId = updatedStage.id,
            name = updatedStage.name,
            formId = updatedStage.formId,
            description = updatedStage.description,
            startDate = updatedStage.startDate,
            endDate = updatedStage.endDate
        ).last()
        return when (resourceState) {
            is ResourceState.Error -> false
            is ResourceState.Success -> true
        }
    }

    private suspend fun addMemberToRepository(
        stageId: String,
        addedMembers: List<User>,
    ): List<Deferred<Boolean>> = coroutineScope {
        addedMembers.map { addedMember ->
            async {
                val resourceState = stageRepository.updateStageMember(
                    stageId = stageId,
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
        stageId: String,
        deleteMemberIds: List<String>,
    ): List<Deferred<Boolean>> = coroutineScope {
        deleteMemberIds.map { memberId ->
            async {
                val resourceState = stageRepository.updateStageMember(
                    stageId = stageId,
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
        if (!validate() || state.value.stage == null || !state.value.isUpdated) return
        val currentState = state.value

        val currentStage = currentState.stage!!
        val currentMembers = currentState.stageUsers

        _state.update { it.copy(status = UiStatus.LOADING) }
        viewModelScope.launch(Dispatchers.IO) {
            val results = mutableListOf<Boolean>()

            if (currentState.isUpdated) {
                results += async {
                    updateStageToRepository(
                        updatedStage = currentStage,
                    )
                }.await()
            }
            if (currentState.addedMemberIds.isNotEmpty()) {
                val addedMemberIds = currentState.addedMemberIds
                val addedMembers = currentMembers.filter { addedMemberIds.contains(it.id) }
                results += addMemberToRepository(
                    stageId = currentStage.id,
                    addedMembers = addedMembers,
                ).awaitAll()
            }
            if (currentState.deletedMemberIds.isNotEmpty()) {
                val deletedMemberIds = currentState.deletedMemberIds
                results += deleteMemberToRepository(
                    stageId = currentStage.id,
                    deleteMemberIds = deletedMemberIds,
                ).awaitAll()
            }

            viewModelScope.launch {
                val result = results.all { it }
                if (result) _state.update { ModifyStageUiState() }
                successHandler(result)
            }
        }
    }

    fun gotError() {
        _state.update { it.copy(error = null) }
    }

    private fun validate(): Boolean {
        val currentStage = state.value.stage
        val startDate = currentStage?.startDate
        val endDate = currentStage?.endDate

        if (currentStage?.name?.isBlank() == true) {
            _state.update { it.copy(error = R.string.error_empty_stage_name) }
            return false
        } else if (startDate == null || endDate == null) {
            _state.update { it.copy(error = R.string.error_empty_startDate_endDate) }
            return false
        } else if (startDate > endDate) {
            _state.update { it.copy(error = R.string.error_start_date_greater_than_end_date) }
            return false
        } else
            return true
    }
}