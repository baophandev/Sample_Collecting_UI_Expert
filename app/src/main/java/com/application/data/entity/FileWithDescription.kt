package com.application.data.entity

import android.net.Uri

data class FileWithDescription(
    val file: Uri,
    val description: String? = null
)
