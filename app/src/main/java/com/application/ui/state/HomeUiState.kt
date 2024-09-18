package com.application.ui.state

import android.net.Uri
import androidx.annotation.StringRes
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.application.data.entity.Project
import com.application.data.entity.ProjectData

data class HomeUiState(
    val projects: SnapshotStateList<Pair<Uri?, Project>> = mutableStateListOf(
        Pair(
            null, Project(
                projectId = "test-project-id",
                data = ProjectData(
                    title = "Project Test",
                    forms = mapOf(),
                    stages = mapOf(),
                    endDate = System.currentTimeMillis(),
                    startDate = System.currentTimeMillis(),
                    memberIds = mapOf(),
                    emailOwner = "email@mail.com",
                    description = "Test project"
                )
            )
        )
    ),
    val init: Boolean = false,
    val loading: Boolean = false,
    @StringRes val error: Int? = null,
    @StringRes val cancel: Int? = null
)