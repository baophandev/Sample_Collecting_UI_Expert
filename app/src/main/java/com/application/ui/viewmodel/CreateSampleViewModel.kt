package com.application.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.R
import com.application.android.utility.state.ResourceState
import com.application.constant.UiStatus
import com.application.data.repository.FieldRepository
import com.application.data.repository.StageRepository
import com.application.ui.state.CreateSampleUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateSampleViewModel @Inject constructor(
    private val stageRepository: StageRepository,
    private val fieldRepository: FieldRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CreateSampleUiState())
    val state = _state.asStateFlow()

    fun loadForm(stageId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            stageRepository.getStage(stageId)
                .onStart { _state.update { it.copy(status = UiStatus.LOADING) } }
                .collectLatest { resourceState ->
                    when (resourceState) {
                        is ResourceState.Error -> _state.update { it.copy(status = UiStatus.ERROR) }
                        is ResourceState.Success -> {
                            val stage = resourceState.data
                            val formId = stage.formId

                            if (formId == null)
                                _state.update { it.copy(status = UiStatus.SUCCESS) }
                            else {
                                val fieldResourceState = fieldRepository.getAllFields(formId).last()
                                if (fieldResourceState is ResourceState.Success)
                                    _state.update {
                                        it.copy(
                                            status = UiStatus.SUCCESS,
                                            formId = formId,
                                            fields = fieldResourceState.data
                                        )
                                    }
                                else _state.update {
                                    it.copy(status = UiStatus.ERROR)
                                }
                            }
                        }
                    }
                }
        }
    }

    fun gotError() {
        _state.update { it.copy(error = null) }
    }

    fun submitSample(
        stageId: String,
        sampleImage: Pair<String, Uri>,
        result: (String) -> Unit,
        isCancelled: () -> Unit,
    ) {
        if (!validate()) {
            _state.update { it.copy(error = R.string.fields_not_validate) }
            return
        }

        // collect fields
        val fields = mutableMapOf<String, String>()


        viewModelScope.launch(Dispatchers.IO) {
        }
    }

    private fun validate(): Boolean {
//        return state.value.fields.all { it..isNotBlank() }
        return true
    }
}