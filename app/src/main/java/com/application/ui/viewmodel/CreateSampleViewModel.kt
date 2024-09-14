package com.application.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.R
import com.application.ui.state.CreateSampleUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateSampleViewModel @Inject constructor(
) : ViewModel() {

    private val _state = MutableStateFlow(CreateSampleUiState())
    val state = _state.asStateFlow()

    fun gotError() {
        _state.update { it.copy(error = null) }
    }

    fun submitSample(
        isProjectOwner: Boolean,
        projectId: String,
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
        state.value.flexFields.removeIf { it.second.isBlank() }
        val fields = mutableMapOf<String, String>()

        fields.putAll(state.value.blockFields.mapIndexed { index, pair ->
            Pair("${pair.first}-$index", pair.second)
        })

        val blockSize = state.value.blockFields.size
        fields.putAll(state.value.flexFields.mapIndexed { index, pair ->
            Pair("${pair.first.trim()}-${blockSize + index}", pair.second)
        })

        viewModelScope.launch(Dispatchers.IO) {
        }
    }

    private fun validate(): Boolean {
        return state.value.blockFields.all { it.second.isNotBlank() }
    }
}