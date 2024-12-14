package com.application.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.R
import com.application.constant.UiStatus
import com.application.data.entity.Answer
import com.application.data.entity.DynamicField
import com.application.data.repository.FieldRepository
import com.application.data.repository.SampleRepository
import com.application.data.repository.StageRepository
import com.application.ui.state.CreateSampleUiState
import com.sc.library.utility.state.ResourceState
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
    private val fieldRepository: FieldRepository,
    private val sampleRepository: SampleRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CreateSampleUiState())
    val state = _state.asStateFlow()

    lateinit var stageId: String

    fun loadFormFromStage(stageId: String) {
        this.stageId = stageId

        viewModelScope.launch(Dispatchers.IO) {
            stageRepository.getStage(stageId)
                .onStart { _state.update { it.copy(status = UiStatus.LOADING) } }
                .collectLatest { rsState ->
                    when (rsState) {
                        is ResourceState.Error -> _state.update {
                            it.copy(status = UiStatus.ERROR, error = rsState.resId)
                        }

                        is ResourceState.Success -> {
                            val stage = rsState.data
                            val formId = stage.formId

                            when (val fieldRsState = fieldRepository.getAllFields(formId).last()) {
                                is ResourceState.Error -> _state.update {
                                    it.copy(status = UiStatus.ERROR, error = fieldRsState.resId)
                                }

                                is ResourceState.Success -> _state.update {
                                    it.copy(
                                        status = UiStatus.SUCCESS,
                                        formId = formId,
                                        answers = fieldRsState.data.map { field ->
                                            Answer(content = "", field = field)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
        }
    }

    fun loadSampleImage(sampleImage: Pair<String, Uri>) {
        _state.update { it.copy(sampleImage = sampleImage) }
    }

    fun gotError() {
        _state.update { it.copy(status = UiStatus.SUCCESS, error = null) }
    }

    fun submitSample(result: (String) -> Unit) {
        val currentState = state.value
        if (!validate()) {
            _state.update { it.copy(error = R.string.fields_not_validate) }
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            sampleRepository.createSample(
                stageId = stageId,
                attachmentUri = currentState.sampleImage!!.second,
                position = "",
                answers = currentState.answers,
                dynamicFields = currentState.dynamicFields
            )
                .onStart { _state.update { it.copy(status = UiStatus.LOADING) } }
                .collectLatest { rsState ->
                    when (rsState) {
                        is ResourceState.Error -> _state.update {
                            it.copy(status = UiStatus.ERROR, error = rsState.resId)
                        }

                        is ResourceState.Success -> {
                            _state.update { it.copy(status = UiStatus.SUCCESS) }
                            viewModelScope.launch { result(rsState.data) }
                        }
                    }
                }
        }
    }

    private fun validate(): Boolean {
        val currentState = state.value
        val answersValidatorResult = currentState.answers.all { it.content.isNotBlank() }
        val dynamicFieldsValidatorResult =
            currentState.dynamicFields.all { it.name.isNotBlank() && it.value.isNotBlank() }

        return answersValidatorResult && dynamicFieldsValidatorResult
    }

    fun updateAnswer(index: Int, newValue: String) {
        val updatedFields = updateFieldHelper(
            supplier = state.value.answers,
            index = index
        ) { it.copy(content = newValue) }
        _state.update { it.copy(answers = updatedFields) }
    }

    fun addDynamicField() {
        val updatedFields = state.value.dynamicFields.toMutableList()
        val currentSize = updatedFields.size
        val newField = DynamicField(
            id = "${System.currentTimeMillis()}-${currentSize}",
            name = "",
            value = "",
        )
        updatedFields.add(newField)
        _state.update { it.copy(dynamicFields = updatedFields) }
    }

    fun updateDynamicFieldName(index: Int, fieldName: String) {
        val updatedFields = updateFieldHelper(
            supplier = state.value.dynamicFields,
            index = index
        ) { it.copy(name = fieldName) }
        _state.update { it.copy(dynamicFields = updatedFields) }
    }

    fun updateDynamicFieldValue(index: Int, fieldValue: String) {
        val updatedFields = updateFieldHelper(
            supplier = state.value.dynamicFields,
            index = index
        ) { it.copy(value = fieldValue) }
        _state.update { it.copy(dynamicFields = updatedFields) }
    }

    private fun <T> updateFieldHelper(
        index: Int,
        supplier: List<T>,
        transformer: (T) -> T
    ): List<T> {
        val mutableApplyList = supplier.toMutableList()
        mutableApplyList.getOrNull(index)?.let { obj ->
            val newObj = transformer(obj)
            mutableApplyList.removeAt(index)
            mutableApplyList.add(index, newObj)
        }
        return mutableApplyList
    }

    fun deleteDynamicField(index: Int) {
        val updatedFields = state.value.dynamicFields.toMutableList()
        updatedFields.removeAt(index)
        _state.update { it.copy(dynamicFields = updatedFields) }
    }

}