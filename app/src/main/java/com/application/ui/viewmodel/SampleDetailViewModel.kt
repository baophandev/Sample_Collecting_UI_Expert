package com.application.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.application.data.entity.Sample
import com.application.ui.state.SampleDetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SampleDetailViewModel @Inject constructor(
) : ViewModel() {

    private val _state = MutableStateFlow(SampleDetailUiState())
    val state = _state.asStateFlow()

    fun loadSample(sample: Sample) {
        _state.update { it.copy(sample = sample) }
    }

}
