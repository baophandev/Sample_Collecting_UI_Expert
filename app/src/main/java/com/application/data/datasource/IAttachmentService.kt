package com.application.data.datasource

import com.application.android.utility.file.FileInfo
import com.application.data.entity.response.AttachmentResponse

interface IAttachmentService {

    suspend fun getAttachment(attachmentId: String): AttachmentResponse
    suspend fun uploadAttachment(attachment: FileInfo): String
    suspend fun deleteAttachment(attachmentId: String): Boolean

}