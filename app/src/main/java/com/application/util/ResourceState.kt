package com.application.util

import androidx.annotation.StringRes

sealed class ResourceState<T> {
    data class Success<T>(val data: T) : ResourceState<T>()
    data class Error<T>(@StringRes val error: Int? = null, val str: String? = null) :
        ResourceState<T>()
}