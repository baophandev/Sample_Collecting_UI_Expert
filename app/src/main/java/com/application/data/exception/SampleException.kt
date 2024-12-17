package com.application.data.exception

sealed class SampleException(
    override val message: String? = null,
    override val cause: Throwable? = null
) : Exception(message, cause) {

    class AttachmentStoringException(
        override val message: String? = null,
        override val cause: Throwable? = null
    ) : SampleException(message, cause)

}