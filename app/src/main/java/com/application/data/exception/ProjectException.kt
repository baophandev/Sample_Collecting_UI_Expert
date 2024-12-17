package com.application.data.exception

sealed class ProjectException(
    override val message: String? = null,
    override val cause: Throwable? = null
) : Exception(message, cause) {

    class AttachmentStoringException(
        override val message: String? = null,
        override val cause: Throwable? = null
    ) : ProjectException(message, cause)

    class UserRetrievingException(
        override val message: String? = null,
        override val cause: Throwable? = null
    ) : PostException(message, cause)

}