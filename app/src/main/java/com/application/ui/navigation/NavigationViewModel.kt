package com.application.ui.navigation

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.application.data.entity.Project
import com.application.data.entity.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class NavigationDataHolder(
    var isProjectOwner: Boolean? = true,
    var user: User? = User(id = "test-user", username = "email@mail.com", name = "Test user"),
    var thumbnailUri: Uri? = null,
    var project: Project? = Project(
        id = "test-project-id",
        name = "Project Test",
        forms = listOf(),
        stages = listOf(),
        endDate = System.currentTimeMillis(),
        startDate = System.currentTimeMillis(),
        memberUsernames = listOf(),
        owner = User(id = "test-user", username = "email@mail.com", name = "Test user"),
        description = "Test project"
    ),
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
//        val isProjectOwner = project?.data?.emailOwner == data.value.user?.username
//        _data.update { it.copy(isProjectOwner = isProjectOwner, project = project) }
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