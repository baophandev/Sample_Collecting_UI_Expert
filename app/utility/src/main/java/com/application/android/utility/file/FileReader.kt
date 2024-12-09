package com.application.android.utility.file

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FileReader(
    private val context: Context,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    suspend fun uriToFile(uri: Uri): FileInfo = withContext(ioDispatcher) {
        val name = context.contentResolver
            .query(uri, null, null, null).use { cursor ->
                val nameIndex = cursor?.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                cursor?.moveToFirst()
                nameIndex?.let(cursor::getString)
            } ?: ""
        val mimeType = context.contentResolver.getType(uri) ?: ""
        val byte = context.contentResolver
            .openInputStream(uri)?.use { inputStream ->
                inputStream.readAllBytes()
            } ?: byteArrayOf()

        FileInfo(
            name = name,
            mimeType = mimeType,
            byte = byte
        )
    }

}