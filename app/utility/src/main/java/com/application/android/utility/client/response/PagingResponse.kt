package com.application.android.utility.client.response

data class PagingResponse<T>(
    val totalPages: Int,
    val totalElements: Long,
    val number: Int,
    val size: Int,
    val numberOfElements: Int,
    val first: Boolean,
    val last: Boolean,
    val content: List<T>
)
