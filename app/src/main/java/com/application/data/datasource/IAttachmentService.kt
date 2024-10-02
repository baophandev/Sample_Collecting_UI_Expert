package com.application.data.datasource

import android.net.Uri
import com.application.data.entity.response.AttachmentResponse

interface IAttachmentService {

    suspend fun getAttachment(attachmentId: String): AttachmentResponse
    suspend fun uploadAttachment(uri: Uri): String

}