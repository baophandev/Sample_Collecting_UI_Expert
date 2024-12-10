package com.application.android.user_library.datasource

import com.application.android.user_library.entity.response.DomainResponse
import com.application.android.user_library.entity.response.LoginResponse
import com.application.android.user_library.entity.response.UserResponse

interface IUserService {
    suspend fun login(
        username: String,
        password: String
    ): LoginResponse

    suspend fun register(
        username: String,
        password: String,
        firstName: String,
        lastName: String,
        email: String,
        gender: String,
        birthDate: String
    ): String // userID
    suspend fun getUser(userId: String): UserResponse

    suspend fun getAllDomains (
        pageNumber: Int = 0,
        pageSize: Int = 6
    ) : List<DomainResponse>

}