package com.application.ui.navigation

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.application.data.entity.Project
import com.application.data.entity.Stage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class NavigationDataHolder(
    val isProjectOwner: Boolean? = true,
    val userId: String? = "test",
    val thumbnailUri: Uri? = null,
    val project: Project? = null,
    val stage: Stage?= null,
    val stageId: String? = null,
    val sample: Pair<String, Uri>? = null
)

@HiltViewModel
class NavigationViewModel @Inject constructor() : ViewModel() {
    private val _data = MutableStateFlow(NavigationDataHolder())
    val data = _data.asStateFlow()

    fun updateUserId(userId: String) {
        _data.update { it.copy(userId = userId) }
    }

    fun updateThumbnailUri(thumbnail: Uri?) {
        _data.update { it.copy(thumbnailUri = thumbnail) }
    }

    fun updateProject(project: Project?) {

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