package com.application.data.datasource.impl

import com.application.data.datasource.IUserService
import com.application.data.entity.response.UserResponse

class UserServiceImpl : IUserService, AbstractClient() {

    override suspend fun getUser(userId: String): UserResponse {
        TODO("Not yet implemented")
    }

}