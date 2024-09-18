package com.application.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.util.ResourceState
import com.application.data.entity.Stage
import com.application.ui.state.ModifyStageUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ModifyStageViewModel @Inject constructor(
) : ViewModel() {
    private val _state = MutableStateFlow(ModifyStageUiState(init = true))
    val state = _state.asStateFlow()

    fun setStage(stage: Stage) {
        stage.emailMembers?.map { it.value }?.let { memberIds ->
            val currentMemberIds = state.value.memberIds.toMutableList()
            currentMemberIds.addAll(memberIds)
            _state.update { it.copy(memberIds = currentMemberIds.toList()) }
        }

        _state.update {
            it.copy(
                init = false,
                title = stage.title ?: "",
                description = stage.description ?: "",
                startDate = stage.startDate,
                endDate = stage.endDate,
                formId = stage.formId ?: "",
            )
        }
    }

    fun updateTitle(title: String) {
        _state.update { it.copy(title = title) }
    }

    fun updateDescription(description: String) {
        _state.update { it.copy(description = description) }
    }

    fun updateDate(date: Long, isStartDate: Boolean) {
        if (isStartDate) {
            _state.update { it.copy(startDate = date) }
        } else {
            _state.update { it.copy(endDate = date) }
        }
    }

    fun updateFormId(formId: String) {
        _state.update { it.copy(formId = formId) }
    }

    fun submit(preStage: Pair<String, Stage>, projectId: String, successHandler: () -> Unit) {
        val collectAction: (ResourceState<Boolean>) -> Unit = { resourceState ->
            when (resourceState) {
                is ResourceState.Success -> viewModelScope.launch { successHandler() }

                is ResourceState.Error -> _state.update {
                    it.copy(loading = false, error = resourceState.error)
                }

                else -> {}
            }
        }

        val curState = state.value
        val newTitle = if (curState.title != preStage.second.title) curState.title else null
        val newDesc =
            if (curState.description != preStage.second.description) curState.description else null

        // convert to date
        val newStartDate =
            if (curState.startDate != preStage.second.startDate) curState.startDate else null
        val newEndDate =
            if (curState.endDate != preStage.second.endDate) curState.endDate else null

        val newEmailMember = curState.memberIds.toList()
        val newFormId = if (curState.formId != preStage.second.formId) curState.formId else null

        viewModelScope.launch(Dispatchers.IO) {
        }
    }
}