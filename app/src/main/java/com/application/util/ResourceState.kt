package com.application.android.utilities

import androidx.annotation.StringRes

sealed class ResourceState<T> {
    class Loading<T> : ResourceState<T>()
    data class Cancel<T>(@StringRes val reason: Int? = null) : ResourceState<T>()
    data class Success<T>(val data: T) : ResourceState<T>()
    data class Error<T>(@StringRes val error: Int? = null, val str: String? = null) :
        ResourceState<T>()
}