package com.application.data.exception

sealed class FormException(
    override val message: String? = null,
    override val cause: Throwable? = null
) : Exception(message, cause) {

    class FieldCreatingException(
        override val message: String? = null,
        override val cause: Throwable? = null
    ) : FormException(message, cause)

}