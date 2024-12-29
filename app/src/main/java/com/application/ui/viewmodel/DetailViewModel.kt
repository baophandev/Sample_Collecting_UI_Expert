package com.application.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.application.constant.ReloadSignal
import com.application.constant.UiStatus
import com.application.data.entity.Form
import com.application.data.entity.Stage
import com.application.data.paging.FormPagingSource
import com.application.data.paging.StagePagingSource
import com.application.data.repository.FormRepository
import com.application.data.repository.ProjectRepository
import com.application.data.repository.StageRepository
import com.application.ui.state.DetailUiState
import io.github.nhatbangle.sc.user.repository.UserRepository
import io.github.nhatbangle.sc.utility.state.ResourceState
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
class DetailViewModel @Inject constructor(
    private val projectRepository: ProjectRepository,
    private val stageRepository: StageRepository,
    private val formRepository: FormRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DetailUiState())
    val state = _state.asStateFlow()

    lateinit var stageFlow: Flow<PagingData<Stage>>
    lateinit var formFlow: Flow<PagingData<Form>>

    fun fetchStages(projectId: String) {
        stageFlow = Pager(
            PagingConfig(
                pageSize = 3,
                enablePlaceholders = false,
                prefetchDistance = 1,
                initialLoadSize = 3,
            )
        ) {
            StagePagingSource(
                projectId = projectId,
                stageRepository = stageRepository
            )
        }.flow
            .cachedIn(viewModelScope)
            .catch { Log.e(TAG, it.message, it) }
    }

    fun fetchProject(projectId: String, skipCached: Boolean = false) {
        _state.update { it.copy(status = UiStatus.LOADING) }
        viewModelScope.launch(Dispatchers.IO) {
            projectRepository.getProject(projectId, skipCached)
                .collectLatest { rsState ->
                    when (rsState) {
                        is ResourceState.Error -> _state.update { it.copy(status = UiStatus.ERROR) }
                        is ResourceState.Success -> {
                            _state.update {
                                it.copy(
                                    project = rsState.data,
                                    status = UiStatus.SUCCESS
                                )
                            }
                        }
                    }
                }
        }
    }

    fun deleteProject(
        projectId: String,
        successHandler: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            projectRepository.deleteProject(projectId = projectId)
                .onStart { _state.update { it.copy(status = UiStatus.LOADING) } }
                .collectLatest { rsState ->
                    when (rsState) {
                        is ResourceState.Success -> {
                            _state.update { it.copy(status = UiStatus.SUCCESS) }
                            viewModelScope.launch { successHandler() }
                        }

                        is ResourceState.Error -> {
                            val error = rsState.resId
                            _state.update { it.copy(status = UiStatus.ERROR, error = error) }
                        }
                    }
                }
        }
    }

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
    }

    fun deleteForm(formId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            formRepository.deleteForm(formId).collectLatest { rsState ->
                when (rsState) {
                    is ResourceState.Success -> reload(ReloadSignal.RELOAD_FORM)
                    is ResourceState.Error -> _state.update { it.copy(error = rsState.resId) }
                }
            }
        }

    }

    fun isProjectOwner(): Boolean {
        val loggedUser = userRepository.loggedUser
        return loggedUser?.id == state.value.project?.owner?.id
    }

    fun reload(signal: ReloadSignal) {
        when (signal) {
            ReloadSignal.RELOAD_STAGE -> state.value.project?.let {
                fetchStages(projectId = it.id)
            }

            ReloadSignal.RELOAD_FORM -> state.value.project?.let {
                fetchForms(projectId = it.id)
            }

            ReloadSignal.RELOAD_PROJECT -> state.value.project?.let {
                fetchProject(projectId = it.id, skipCached = true)
            }

            else -> {}
        }
    }

    companion object {
        const val TAG = "DetailViewModel"
    }

}
