package com.application.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.R
import com.application.constant.UiStatus
import com.application.data.entity.Field
import com.application.data.entity.Form
import com.application.data.repository.FieldRepository
import com.application.data.repository.FormRepository
import com.application.ui.state.ModifyFormUiState
import com.application.util.Validation
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
class ModifyFormViewModel @Inject constructor(
    private val formRepository: FormRepository,
    private val fieldRepository: FieldRepository
) : ViewModel() {
    private val _state = MutableStateFlow(ModifyFormUiState())
    val state = _state.asStateFlow()

    fun loadModifiedForm(form: Form) {
        _state.update { it.copy(status = UiStatus.SUCCESS, form = form) }
    }

    fun loadAllModifiedFields(formId: String) {
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

    fun updateTitle(title: String) {
        if (!Validation.checkNormalText(title)) return

        val currentForm = state.value.form
        _state.update { it.copy(form = currentForm?.copy(title = title), isFormUpdated = true) }
    }

//    fun updateDescription(description: String) {
//        val currentForm = state.value.form
//        _state.update {
//            it.copy(
//                form = currentForm?.copy(description = description),
//                isFormUpdated = true
//            )
//        }
//    }

    fun addNewField() {
        state.value.form?.let { currentForm ->
            val currentFields = state.value.fields
            val mutableFields = currentFields.toMutableList()
            // add temporary field
            val temporaryId = "${System.currentTimeMillis()}-tmp"
            mutableFields.add(Field(temporaryId, currentFields.size, "", currentForm.id))

            val addedFieldIds = state.value.addedFieldIds.toMutableList()
            addedFieldIds.add(temporaryId)
            _state.update { it.copy(fields = mutableFields, addedFieldIds = addedFieldIds) }
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

    fun submit(successHandler: (Boolean) -> Unit) {
        val currentState = state.value
        if (!validateFields() || currentState.form == null) return

        val currentForm = currentState.form
        val currentFields = currentState.fields

        _state.update { it.copy(status = UiStatus.LOADING) }

        viewModelScope.launch(Dispatchers.IO) {
            // chỗ này cập nhật thông tin của form, không bao gồm các field
            if (state.value.isFormUpdated)
                updateFormToRepository(updatedForm = currentForm, successHandler = successHandler)
            if (currentState.addedFieldIds.isNotEmpty()) {
                // chỗ này kiểm tra các field được thêm
                val addedFieldIds = currentState.addedFieldIds
                val addedFields = currentFields.filter { addedFieldIds.contains(it.id) }
                addFieldToRepository(
                    formId = currentForm.id,
                    addedFields = addedFields,
                    successHandler = successHandler
                )
            }
            if (currentState.updatedFieldIds.isNotEmpty()) {
                // chỗ này kiểm tra các field được cập nhật
                val updatedFieldIds = currentState.updatedFieldIds
                val updatedFields = currentFields.filter { updatedFieldIds.contains(it.id) }
                updateFieldToRepository(
                    updatedFields = updatedFields,
                    successHandler = successHandler
                )
            }
            if (currentState.deletedFieldIds.isNotEmpty()) {
                // chỗ này kiểm tra các field bị xóa
                val deletedFieldIds = currentState.deletedFieldIds
                deleteFieldToRepository(
                    deleteFieldIds = deletedFieldIds,
                    successHandler = successHandler
                )
            }
        }
    }

    private suspend fun updateFormToRepository(
        updatedForm: Form,
        successHandler: (Boolean) -> Unit
    ) {
        formRepository.updateForm(
            formId = updatedForm.id,
            title = updatedForm.title,
            description = updatedForm.description
        ).collectLatest { resourceState ->
            when (resourceState) {
                is ResourceState.Error -> _state.update {
                    it.copy(
                        status = UiStatus.SUCCESS,
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
    }

    private suspend fun addFieldToRepository(
        formId: String,
        addedFields: List<Field>,
        successHandler: (Boolean) -> Unit
    ) {
        addedFields.forEachIndexed { index, newField ->
            fieldRepository.createField(
                formId = formId,
                name = newField.name,
                numberOrder = index
            ).collectLatest { resourceState ->
                when (resourceState) {
                    is ResourceState.Error -> _state.update {
                        it.copy(
                            status = UiStatus.SUCCESS,
                            error = resourceState.resId
                        )
                    }

                    is ResourceState.Success -> {
                        _state.update { it.copy(status = UiStatus.SUCCESS) }
                        viewModelScope.launch {
                            successHandler(true)
                        }
                    }
                }
            }
        }
    }

    private suspend fun updateFieldToRepository(
        updatedFields: List<Field>,
        successHandler: (Boolean) -> Unit
    ) {
        updatedFields.forEachIndexed { index, updatedField ->
            fieldRepository.updateField(
                fieldId = updatedField.id,
                fieldName = updatedField.name,
                numberOrder = index
            ).collectLatest { resourceState ->
                when (resourceState) {
                    is ResourceState.Error -> _state.update {
                        it.copy(
                            status = UiStatus.SUCCESS,
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
        }
    }

    private suspend fun deleteFieldToRepository(
        deleteFieldIds: List<String>,
        successHandler: (Boolean) -> Unit
    ) {
        deleteFieldIds.forEach { deletedFieldId ->
            fieldRepository.deleteField(fieldId = deletedFieldId)
                .collectLatest { resourceState ->
                    when (resourceState) {
                        is ResourceState.Error -> _state.update {
                            it.copy(
                                status = UiStatus.SUCCESS,
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
        }
    }

    fun gotError() {
        _state.update { it.copy(error = null) }
    }

    private fun validateFields(): Boolean {
        val currentState = state.value
        if (currentState.form?.title?.isBlank() == true) {
            _state.update { it.copy(error = R.string.error_empty_form_name) }
            return false
        } else if (currentState.fields.isEmpty()) {
            _state.update { it.copy(error = R.string.error_empty_field_of_form) }
            return false
        } else if (currentState.addedFieldIds.any { it.isBlank() } || currentState.fields.any { it.name.isBlank() }) {
            _state.update { it.copy(error = R.string.error_empty_field_name) }
            return false
        } else return true
    }
}




