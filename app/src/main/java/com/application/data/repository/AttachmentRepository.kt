package com.application.data.repository

import android.net.Uri
import android.util.Log
import com.application.data.datasource.IAttachmentService
import com.application.data.entity.Attachment
import com.application.util.ResourceState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class AttachmentRepository(
    private val attachmentService: IAttachmentService
) {

    fun getAttachment(attachmentId: String): Flow<ResourceState<Attachment>> {
        return flowOf()
//        return flow<ResourceState<Attachment>> {
//            val response = attachmentService.getAttachment(attachmentId)
//            val attachment = Attachment(
//                id = attachmentId,
//                name = response.fileName,
//                type = response.fileType,
//                url = response.downloadPath
//            )
//            emit(ResourceState.Success(attachment))
//        }.catch {
//            Log.e(TAG, it.message ?: "Unknown exception")
//            emit(ResourceState.Error(message = "Cannot get attachment"))
//        }
    }

    fun storeAttachment(uri: Uri) : Flow<ResourceState<String>> {
        return flowOf()
//        return flow<ResourceState<String>> {
//            val attachmentId = attachmentService.uploadAttachment(uri)
//            emit(ResourceState.Success(attachmentId))
//        }.catch {
//            Log.e(TAG, it.message ?: "Unknown exception")
//            emit(ResourceState.Error(message = "Cannot store attachment"))
//        }
    }

    companion object {
        const val TAG = "AttachmentRepository"
    }

}