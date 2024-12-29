package com.application.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.constant.UiStatus
import com.application.data.repository.SampleRepository
import com.application.ui.state.SampleDetailUiState
import io.github.nhatbangle.sc.utility.state.ResourceState
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
class SampleDetailViewModel @Inject constructor(
    private val sampleRepository: SampleRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SampleDetailUiState())
    val state = _state.asStateFlow()

    fun loadSample(sampleId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            sampleRepository.getSample(sampleId)
                .onStart { _state.update { it.copy(status = UiStatus.LOADING) } }
                .collectLatest { resourceState ->
                    when (resourceState) {
                        is ResourceState.Error -> _state.update {
                            it.copy(
                                status = UiStatus.ERROR,
                                error = resourceState.resId
                            )
                        }

                        is ResourceState.Success -> {
                            _state.update {
                                it.copy(
                                    status = UiStatus.SUCCESS,
                                    sample = resourceState.data
                                )
                            }
                        }
                    }
                }
        }
    }

}
