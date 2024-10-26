package com.application.data.datasource.impl

import android.net.Uri
import com.application.data.datasource.IAttachmentService
import com.application.data.entity.response.AttachmentResponse

class AttachmentServiceImpl : IAttachmentService, AbstractClient() {

    override suspend fun getAttachment(attachmentId: String): AttachmentResponse {
        TODO("Not yet implemented")
    }

    override suspend fun uploadAttachment(uri: Uri): String {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAttachment(attachmentId: String): Boolean {
        TODO("Not yet implemented")
    }

}