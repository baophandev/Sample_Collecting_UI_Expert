package com.application.data.entity

import android.net.Uri
import java.sql.Timestamp

data class Sample(
    val id: String,
    val image: Uri,
    val position: String? = null,
    val createdAt: Timestamp,
    val projectId: String,
    val stageId: String,
    val answers: List<Answer>,
    val dynamicFields: List<DynamicField>? = null,
//    val writtenBy: String? = null,
)