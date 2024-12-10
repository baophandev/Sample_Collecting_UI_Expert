package com.application.android.user_library.exception

sealed class UserException(
    override val message: String? = null,
    override val cause: Throwable? = null
) : Exception(message, cause) {

    class JWTDecodingException(
        override val message: String? = null,
        override val cause: Throwable? = null
    ) : UserException(message, cause)

    class ScopeInvalidException(
        override val message: String? = null,
        override val cause: Throwable? = null
    ) : UserException(message, cause)

}