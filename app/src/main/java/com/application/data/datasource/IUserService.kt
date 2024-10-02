package com.application.data.datasource

import com.application.data.entity.response.UserResponse

interface IUserService {

    suspend fun getUser(userId: String): UserResponse

}