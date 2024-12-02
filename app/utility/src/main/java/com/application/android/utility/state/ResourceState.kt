package com.application.android.utility.state

import androidx.annotation.StringRes

sealed class ResourceState<T> {
    data class Success<T>(val data: T) : ResourceState<T>()
    data class Error<T>(@StringRes val resId: Int? = null, val message: String? = null) :
        ResourceState<T>()
}