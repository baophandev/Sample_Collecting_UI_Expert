package com.application.android.utility.file

data class FileInfo(
    val name: String,
    val mimeType: String,
    val byte: ByteArray,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FileInfo

        if (name != other.name) return false
        if (mimeType != other.mimeType) return false
        if (!byte.contentEquals(other.byte)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + mimeType.hashCode()
        result = 31 * result + byte.contentHashCode()
        return result
    }
}
