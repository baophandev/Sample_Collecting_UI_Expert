package com.application.data.datasource

import com.application.data.entity.response.AttachmentResponse
import com.application.util.FileInfo

interface IAttachmentService {

    suspend fun getAttachment(attachmentId: String): AttachmentResponse
    suspend fun uploadAttachment(attachment: FileInfo): String
    suspend fun deleteAttachment(attachmentId: String): Boolean

}