package com.application.data.repository

import com.application.data.datasource.IUserService
import com.application.data.entity.User
import com.application.util.ResourceState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class UserRepository(
    private val userService: IUserService
) {
    private val cachedUsers = mutableMapOf<String, User>()
    private val loggedUserKey = "LOGGED_USER_KEY"

    fun login(username: String, password: String) {
        cachedUsers[loggedUserKey] = User(
            id = "user",
            username = username,
            name = "Unknown User"
        )
    }

    fun getLoggedUser(): User? {
//        return cachedUsers[loggedUserKey]
        return User(
            id = "user",
            username = "test",
            name = "Unknown User"
        )
    }

    /**
     * Get user information from server.
     *
     * Note: User information will be cached in memory.
     */
    fun getUser(userId: String): Flow<ResourceState<User>> {
        if (cachedUsers.containsKey(userId))
            return flowOf(ResourceState.Success(cachedUsers[userId]!!))

        return flowOf(
            ResourceState.Success(
                User(
                    id = "user",
                    username = "unknown",
                    name = "Unknown User"
                )
            )
        )
//        return flow<ResourceState<User>> {
//            val userResponse = userService.getUser(userId)
//            val user = User(
//                id = userResponse.id,
//                username = userResponse.username,
//                name = userResponse.name
//            )
//
//            cachedUsers[userId] = user
//            emit(ResourceState.Success(user))
//        }.catch {
//            Log.e(TAG, it.message ?: "Unknown exception")
//            emit(ResourceState.Error(message = it.message))
//        }
    }

    companion object {
        const val TAG = "UserRepository"
        val DEFAULT_USER = User(id = "unknown", username = "unknown", name = "Unknown User")
    }

}