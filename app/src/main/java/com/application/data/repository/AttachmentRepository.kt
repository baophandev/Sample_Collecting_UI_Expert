package com.application.data.repository

import android.net.Uri
import android.util.Log
import com.application.data.datasource.IAttachmentService
import com.application.data.entity.Attachment
import com.application.util.FileReader
import com.application.util.ResourceState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class AttachmentRepository(
    private val fileReader: FileReader,
    private val attachmentService: IAttachmentService
) {
    private val cachedAttachments: MutableMap<String, Attachment> = mutableMapOf()

    fun getAttachment(
        attachmentId: String,
        skipCached: Boolean = false
    ): Flow<ResourceState<Attachment>> {
        if (!skipCached && cachedAttachments.containsKey(attachmentId))
            return flowOf(ResourceState.Success(cachedAttachments[attachmentId]!!))

        return flow<ResourceState<Attachment>> {
            val response = attachmentService.getAttachment(attachmentId)
            val attachment = Attachment(
                id = attachmentId,
                name = response.fileName,
                type = response.fileType,
                url = response.downloadPath
            )
            cachedAttachments[attachmentId] = attachment
            emit(ResourceState.Success(attachment))
        }.catch {
            Log.e(TAG, it.message, it)
            emit(ResourceState.Error(message = "Cannot get attachment"))
        }
    }

    fun storeAttachment(attachment: Uri): Flow<ResourceState<String>> {
        return flow<ResourceState<String>> {
            val file = fileReader.uriToFile(attachment)
            val attachmentId = attachmentService.uploadAttachment(file)
            emit(ResourceState.Success(attachmentId))
        }.catch {
            Log.e(TAG, it.message, it)
            emit(ResourceState.Error(message = "Cannot store attachment"))
        }
    }

    fun deleteAttachment(attachmentId: String): Flow<ResourceState<Boolean>> {
        cachedAttachments.remove(attachmentId)

        return flow<ResourceState<Boolean>> {
            val isSuccess = attachmentService.deleteAttachment(attachmentId)
            emit(ResourceState.Success(isSuccess))
        }.catch {
            Log.e(TAG, it.message, it)
            emit(ResourceState.Error(message = "Cannot store attachment"))
        }
    }

    companion object {
        const val TAG = "AttachmentRepository"
    }

}