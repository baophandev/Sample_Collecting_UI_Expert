package com.application.data.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val uid: String,
    val email: String,
    val name: String
) : Parcelable

/**
 * @param ownProjects Map.Entry(projectId, true)
 * @param joinProjects Map.Entry(projectId, true)
 */
data class UserData(
    val ownProjects: Map<String, Boolean>? = null,
    val joinProjects: Map<String, Boolean>? = null
)