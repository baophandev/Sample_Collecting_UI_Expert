package com.application.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.application.constant.UiStatus
import com.application.data.datasource.IProjectService
import com.application.data.entity.Form
import com.application.data.paging.FormPagingSource
import com.application.data.paging.ProjectPagingSource
import com.application.data.repository.FormRepository
import com.application.util.ResourceState
import com.application.data.repository.StageRepository
import com.application.ui.state.ModifyStageUiState
import com.application.ui.viewmodel.HomeViewModel.Companion.TAG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ModifyStageViewModel @Inject constructor(
    private val stageRepository: StageRepository,
    private val projectService: IProjectService,
) : ViewModel() {
    private val _state = MutableStateFlow(ModifyStageUiState())
    val state = _state.asStateFlow()

    lateinit var flow: Flow<PagingData<Form>>

    fun loadStage(projectId:String, stageId: String) {
        _state.update { it.copy(status = UiStatus.LOADING, isUpdated = false) }
        viewModelScope.launch(Dispatchers.IO) {
            stageRepository.getStage(stageId).collectLatest { resourceState ->
                when (resourceState) {
                    is ResourceState.Success -> _state.update {
                        it.copy(status = UiStatus.SUCCESS, stage = resourceState.data)
                    }

                    is ResourceState.Error -> _state.update {
                        it.copy(status = UiStatus.ERROR, error = resourceState.resId)
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
            FormPagingSource(projectId, projectService)
        }.flow
            .cachedIn(viewModelScope)
            .catch { Log.e(TAG, it.message, it) }
    }

    fun updateStageName(name: String) {
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

    fun updateFormId(formId: String) {
        val currentStage = state.value.stage
        _state.update { it.copy(stage = currentStage?.copy(formId = formId), isUpdated = true) }
    }

    fun submit(successHandler: (Boolean) -> Unit) {
        if (validate() || state.value.stage == null || !state.value.isUpdated) return
        val currentStage = state.value.stage!!

        viewModelScope.launch(Dispatchers.IO) {
            stageRepository.updateStage(
                stageId = currentStage.id,
                name = currentStage.name,
                description = currentStage.description,
                startDate = currentStage.startDate,
                endDate = currentStage.endDate,
                formId = currentStage.formId,
                projectOwnerId = currentStage.projectOwnerId
            )
                .onStart { _state.update { it.copy(status = UiStatus.LOADING) } }
                .collectLatest { resourceState ->
                    when (resourceState) {
                        is ResourceState.Error -> _state.update {
                            it.copy(status = UiStatus.ERROR, error = resourceState.resId)
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
    }
    private fun validate(): Boolean {
        val currentProject = state.value.stage
        val startDate = currentProject?.startDate
        val endDate = currentProject?.endDate

        if (currentProject?.name == null ||
            (startDate != null && endDate != null && startDate > endDate)
        )
            return false
        return currentProject.name.isBlank()
    }
}