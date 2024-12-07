package com.application.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.R
import com.application.android.utility.state.ResourceState
import com.application.constant.UiStatus
import com.application.data.entity.Answer
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
                                            answers = fieldResourceState.data.map { field ->
                                                Answer(content = "", field = field)
                                            }
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

    fun updateDataOfField(data: String) {
        _state.update { it.copy() }
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

    fun updateAnswer(index: Int, newValue: String) {
        val currentAnswers = state.value.answers.toMutableList()
        if (index >= currentAnswers.size) return

        val newAnswer = currentAnswers[index].copy(content = newValue)
        currentAnswers.removeAt(index)
        currentAnswers.add(index, newAnswer)
        _state.update { it.copy(answers = currentAnswers) }
    }

    fun updateDynamicFieldName(index: Int, fieldName: String) {
        TODO("Not yet implemented")
    }

    fun updateDynamicFieldValue(index: Int, fieldValue: String) {
        TODO("Not yet implemented")
    }

    fun deleteDynamicField(index: Int) {
        TODO("Not yet implemented")
    }
}