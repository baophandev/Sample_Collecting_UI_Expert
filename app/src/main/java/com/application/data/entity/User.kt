package com.application.data.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val id: String,
    val username: String,
    val name: String
) : Parcelable