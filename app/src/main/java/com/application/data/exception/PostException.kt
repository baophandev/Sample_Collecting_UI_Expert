package com.application.data.exception

sealed class PostException(
    override val message: String? = null,
    override val cause: Throwable? = null
) : Exception(message, cause) {

    class UserRetrievingException(
        override val message: String? = null,
        override val cause: Throwable? = null
    ) : PostException(message, cause)

    class AttachmentRetrievingException(
        override val message: String? = null,
        override val cause: Throwable? = null
    ) : PostException(message, cause)

    class AttachmentStoringException(
        override val message: String? = null,
        override val cause: Throwable? = null
    ) : PostException(message, cause)

}