package com.application.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.data.entity.Project
import com.application.ui.state.DetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
) : ViewModel() {

    // luu vao day
    private val _state = MutableStateFlow(DetailUiState(init = true))
    val state = _state.asStateFlow() // day la bien cho ben ngoai doc va su dung

    // lay project va cap nhat vao state
    fun setProject(thumbnailUri: Uri? = null, project: Project) {
//        project.stages?.map { stage ->
//            Pair(stage.key, Pair(stage.value.title!!, stage.value.description))
//        }?.let { stages ->
//            state.value.stages.clear()
//            state.value.stages.addAll(stages)
//        }
//        project.data.forms?.map { form ->
//            Pair(form.key, form.value.name!!)
//        }?.let { forms ->
//            state.value.forms.clear()
//            state.value.forms.addAll(forms)
//        }
//
//        val thumbnail = thumbnailUri?.let { Pair(project.data.thumbnailPath!!, it) }
//        _state.update {
//            it.copy(
//                init = false,
//                thumbnail = thumbnail,
//                title = project.data.title,
//                description = project.data.description,
//            )
//        }
    }

    fun deleteProject(
        projectId: String,
        emailMembers: List<String>? = null,
        successHandler: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
        }
    }

    fun deleteForm(projectId: String, formId: String) {
        viewModelScope.launch(Dispatchers.IO) {
        }
    }

    companion object {
        const val TAG = "DetailViewModel"
    }
}
