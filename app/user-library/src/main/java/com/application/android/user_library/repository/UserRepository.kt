package com.application.android.user_library.repository

import android.util.Log
import com.application.android.utility.state.ResourceState
import com.application.android.user_library.datasource.IUserService
import com.application.android.user_library.entity.Domain
import com.application.data.entity.LoginCertificate
import com.application.data.entity.User
import com.application.android.user_library.entity.response.DomainResponse
import com.application.android.user_library.entity.response.UserResponse
import io.github.nefilim.kjwt.JWSRSA256Algorithm
import io.github.nefilim.kjwt.JWT
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class UserRepository(
    private val service: IUserService
) {
    private val cachedUsers = mutableMapOf<String, User>()
    private lateinit var _currentUser: User
    private lateinit var _currentLoginCertificate: LoginCertificate

    val loggedUser: User?
        get() {
            return if (this::_currentUser.isInitialized)
                _currentUser
            else null
        }
    val loggedCertificate: LoginCertificate?
        get() {
            return if (this::_currentUser.isInitialized)
                _currentLoginCertificate
            else null
        }

    suspend fun login(username: String, password: String): Flow<ResourceState<LoginCertificate>> {
        // Check if the user is already cached

        return flow<ResourceState<LoginCertificate>> {
            val loginResponse = service.login(username, password)
            _currentLoginCertificate = LoginCertificate(
                loginResponse.access_token,
                loginResponse.expires_in,
                loginResponse.refresh_expires_in,
                loginResponse.token_type
            )

            val userId = JWT.decodeT(_currentLoginCertificate.accessToken, JWSRSA256Algorithm)
                .map { it.subject().orNull() }
                .orNull() ?: throw Error("Cannot decode JWT token to get user ID.")
            val userResponse = service.getUser(userId)
            _currentUser = mapResponseToUser(userResponse)

            emit(ResourceState.Success(_currentLoginCertificate))
        }.catch { exception ->
            Log.e(TAG, exception.message, exception)
            emit(ResourceState.Error(message = exception.message))
        }
    }

    suspend fun register(
        username: String,
        password: String,
        firstName: String,
        lastName: String,
        email: String,
        gender: String,
        birthDate: String
    ): Flow<ResourceState<String>> {
        return flow<ResourceState<String>> {
            val registerResponse = service.register(
                username = username,
                password = password,
                firstName = firstName,
                lastName = lastName,
                email = email,
                gender = gender,
                birthDate = birthDate
            )

//
            emit(ResourceState.Success(registerResponse))
        }.catch { exception ->
            Log.e(TAG, exception.message, exception)
            emit(ResourceState.Error(message = exception.message))
        }
    }

    /**
     * Get user information from server.
     *
     * Note: User information will be cached in memory.
     */
    fun getUser(userId: String): Flow<ResourceState<User>> {
        return flow<ResourceState<User>> {
            val userResponse = service.getUser(userId)
            val user = mapResponseToUser(userResponse)

            cachedUsers[userId] = user
            emit(ResourceState.Success(user))
        }.catch {
            Log.e(TAG, it.message, it)
            emit(ResourceState.Error(message = it.message))
        }
    }

    private fun mapResponseToUser(userResponse: UserResponse): User {
        return User(
            id = userResponse.id,
            firstName = userResponse.firstName,
            lastName = userResponse.firstName,
            email = userResponse.email
        )
    }

    fun getAllDomains(): Flow<ResourceState<List<Domain>>> {
        return flow<ResourceState<List<Domain>>> {
            val domainList = service.getAllDomains()
                .map(this@UserRepository::mapResponseToDomain)
            emit(ResourceState.Success(domainList))
        }.catch {
            Log.e(TAG, it.message, it)
            emit(ResourceState.Error(message = "Cannot get domain"))
        }
    }

    private fun mapResponseToDomain(response: DomainResponse): Domain {
        return Domain(
            id = response.id,
            name = response.name,
            description = response.description
        )
    }

    companion object {
        const val TAG = "UserRepository"
    }

}