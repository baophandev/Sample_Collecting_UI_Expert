package com.application.data.datasource.impl

import com.application.constant.ServiceHost
import com.application.data.datasource.IAttachmentService
import com.application.data.entity.response.AttachmentResponse
import com.application.util.FileInfo
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.get
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode

class AttachmentServiceImpl : IAttachmentService, AbstractClient() {

//    private val client = getClient("http://10.0.2.2:8080/api/file/")
    private val client = getClient("http://${ServiceHost.GATEWAY_SERVER}/api/file/")

    override suspend fun getAttachment(attachmentId: String): AttachmentResponse {
        val response: AttachmentResponse = client.get(urlString = "information/$attachmentId")
            .body()
        return response.copy(
            downloadPath = "http://${ServiceHost.GATEWAY_SERVER}/api/file/${response.fileId}"
        )
    }

    override suspend fun uploadAttachment(attachment: FileInfo): String {
        return client.submitFormWithBinaryData(
            formData = formData {
                append("file", attachment.byte, Headers.build {
                    append(HttpHeaders.ContentType, attachment.mimeType)
                    append(HttpHeaders.ContentDisposition, "filename=\"${attachment.name}\"")
                })
            }
        ).body()
    }

    override suspend fun deleteAttachment(attachmentId: String): Boolean {
        return client.delete(urlString = attachmentId)
            .status == HttpStatusCode.OK
    }

}