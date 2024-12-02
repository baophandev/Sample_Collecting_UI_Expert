package com.application.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.constant.UiStatus
import com.application.data.entity.Field
import com.application.data.repository.FieldRepository
import com.application.data.repository.FormRepository
import com.application.ui.state.ModifyFormUiState
import com.application.util.ResourceState
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
class ModifyFormViewModel @Inject constructor(
    private val formRepository: FormRepository,
    private val fieldRepository: FieldRepository
) : ViewModel() {
    private val _state = MutableStateFlow(ModifyFormUiState())
    val state = _state.asStateFlow()

    fun loadModifiedForm(formId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            formRepository.getForm(formId)
                .onStart {
                    _state.update {
                        it.copy(
                            status = UiStatus.LOADING,
                            isFormUpdated = false
                        )
                    }
                }
                .collectLatest { resourceState ->
                    when (resourceState) {
                        is ResourceState.Success -> _state.update {
                            it.copy(status = UiStatus.SUCCESS, form = resourceState.data)
                        }

                        is ResourceState.Error -> _state.update {
                            it.copy(status = UiStatus.ERROR, error = resourceState.resId)
                        }
                    }
                }
        }
    }

    fun loadAllModifiedField(formId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            fieldRepository.getAllFields(formId)
                .onStart {
                    _state.update {
                        it.copy(
                            status = UiStatus.LOADING,
                            isFormUpdated = false
                        )
                    }
                }
                .collectLatest { resourceState ->
                    when (resourceState) {
                        is ResourceState.Success -> _state.update {
                            it.copy(status = UiStatus.SUCCESS, fields = resourceState.data)
                        }

                        is ResourceState.Error -> _state.update {
                            it.copy(status = UiStatus.ERROR, error = resourceState.resId)
                        }
                    }
                }
        }
    }

    fun addNewField() {
        state.value.form?.let { currentForm ->
            val currentFields = state.value.fields
            val mutableFields = currentFields.toMutableList()
            // add temporary field
            val temporaryId = "${System.currentTimeMillis()}-tmp"
            mutableFields.add(Field(temporaryId, "", currentForm.id))

            val addedFieldIds = state.value.addedFieldIds.toMutableList()
            addedFieldIds.add(temporaryId)
            _state.update { it.copy(fields = mutableFields, addedFieldIds = addedFieldIds) }
        }
    }

    fun updateTitle(title: String) {
        val currentForm = state.value.form
        _state.update { it.copy(form = currentForm?.copy(title = title), isFormUpdated = true) }
    }

    fun updateDescription(description: String) {
        val currentForm = state.value.form
        _state.update {
            it.copy(
                form = currentForm?.copy(description = description),
                isFormUpdated = true
            )
        }
    }

    fun submit(successHandler: (Boolean) -> Unit) {
        val latestAction : (ResourceState<Boolean>) -> Unit = { resourceState ->
            when (resourceState) {
                is ResourceState.Error -> _state.update {
                    it.copy(
                        status = UiStatus.ERROR,
                        error = resourceState.resId
                    )
                }

                is ResourceState.Success -> {
                    _state.update { it.copy(status = UiStatus.SUCCESS) }
                    viewModelScope.launch {
                        successHandler(resourceState.data)
                    }
                }
            }
        }

        state.value.form?.let { currentForm ->


            viewModelScope.launch(Dispatchers.IO) {
                // chỗ này cập nhật thông tin của form, không bao gồm các field
                if (state.value.isFormUpdated)
                    formRepository.updateForm(
                        formId = currentForm.id,
                        title = currentForm.title,
                        description = currentForm.description
                    )
                        .onStart { _state.update { it.copy(status = UiStatus.LOADING) } }
                        .collectLatest(latestAction)
                else if (state.value.addedFieldIds.isNotEmpty()) {
                    // chỗ này kiểm tra các field được thêm
                    val addedField = state.value.addedFieldIds.map {

                    }
                } else if (state.value.updatedFieldIds.isNotEmpty()) {
                    // chỗ này kiểm tra các field được cập nhật

                } else if (state.value.deletedFieldIds.isNotEmpty()) {
                    // chỗ này kiểm tra các field bị xóa
                }
                TODO("Using tracking lists to perform actions accordingly")

            }
        }
    }

    fun updateFieldName(fieldIndex: Int, fieldName: String) {
        val currentFields = state.value.fields
        currentFields.getOrNull(fieldIndex)?.let { field ->
            val mutableFields = currentFields.toMutableList()
            mutableFields.removeAt(fieldIndex)
            mutableFields.add(fieldIndex, field.copy(name = fieldName))

            if (field.id.contains("-tmp"))
                _state.update { it.copy(fields = mutableFields) }
            else {
                val updatedFieldIds = state.value.updatedFieldIds.toMutableSet()
                updatedFieldIds.add(field.id)
                _state.update { it.copy(fields = mutableFields, updatedFieldIds = updatedFieldIds) }
            }
        }
    }

    fun deleteField(fieldIndex: Int) {
        val currentFields = state.value.fields
        currentFields.getOrNull(fieldIndex)?.let { field ->
            val mutableFields = currentFields.toMutableList()
            mutableFields.removeAt(fieldIndex)

            val deletedFieldIds = state.value.deletedFieldIds.toMutableList()
            deletedFieldIds.add(field.id)
            _state.update { it.copy(fields = mutableFields, deletedFieldIds = deletedFieldIds) }
        }
    }

}

