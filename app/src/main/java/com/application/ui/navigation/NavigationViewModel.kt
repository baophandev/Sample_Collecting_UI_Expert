package com.application.ui.navigation

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.data.entity.Project
import com.application.data.entity.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NavigationDataHolder(
    var isProjectOwner: Boolean? = null,
    var user: User? = null,
    var thumbnailUri: Uri? = null,
    var project: Project? = null,
    var stageId: String? = null,
    var sample: Pair<String, Uri>? = null
)

@HiltViewModel
class NavigationViewModel @Inject constructor() : ViewModel() {
    private val _data = MutableStateFlow(NavigationDataHolder())
    val data = _data.asStateFlow()

    fun updateUser(user: User?) {
        _data.update { it.copy(user = user) }
    }

    fun updateThumbnailUri(thumbnail: Uri?) {
        _data.update { it.copy(thumbnailUri = thumbnail) }
    }

    fun updateProject(project: Project?) {
        project?.data?.thumbnailPath?.let { thumbnailPath ->
            if (thumbnailPath != data.value.project?.data?.thumbnailPath) {
                viewModelScope.launch(Dispatchers.IO) {

                }
            }
        }
        val isProjectOwner = project?.data?.emailOwner == data.value.user?.email
        _data.update { it.copy(isProjectOwner = isProjectOwner, project = project) }
    }

    fun updateStageId(stageId: String?) {
        _data.update { it.copy(stageId = stageId) }
    }

    /**
     * @param sample Pair(sampleId, sampleUri)
     */
    fun updateSample(sample: Pair<String, Uri>?) {
        _data.update { it.copy(sample = sample) }
    }
}