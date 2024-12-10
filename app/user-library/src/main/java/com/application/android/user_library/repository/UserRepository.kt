package com.application.android.user_library.repository

import android.util.Log
import com.application.android.user_library.R
import com.application.android.user_library.datasource.IUserService
import com.application.android.user_library.entity.Domain
import com.application.android.user_library.entity.LoginCertificate
import com.application.android.user_library.entity.User
import com.application.android.user_library.entity.response.DomainResponse
import com.application.android.user_library.entity.response.UserResponse
import com.application.android.user_library.exception.UserException
import com.application.android.utility.state.ResourceState
import io.github.nefilim.kjwt.JWSRSA256Algorithm
import io.github.nefilim.kjwt.JWT
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class UserRepository(
    private val service: IUserService
) {
    private val cachedUsers = mutableMapOf<String, User>()

    private var _currentUser: User? = null
    private var _currentLoginCertificate: LoginCertificate? = null

    val loggedUser: User? = _currentUser
    val loggedCertificate: LoginCertificate? = _currentLoginCertificate

    suspend fun login(
        username: String,
        password: String,
        requiredScopes: List<String>? = null
    ): Flow<ResourceState<LoginCertificate>> = flow<ResourceState<LoginCertificate>> {
        val loginResponse = service.login(username, password)
        val loginCert = LoginCertificate(
            loginResponse.access_token,
            loginResponse.expires_in,
            loginResponse.refresh_expires_in,
            loginResponse.token_type
        )

        val (userId, scope) = JWT.decodeT(loginCert.accessToken, JWSRSA256Algorithm)
            .map {
                val userId = it.subject().orNull()
                    ?: throw UserException.JWTDecodingException("Cannot get user ID from JWT token.")
                val scope = if (requiredScopes != null)
                    it.claimValue("scope").orNull()
                        ?: throw UserException.JWTDecodingException("Cannot get scope from JWT token.")
                else null
                Pair(userId, scope)
            }.orNull() ?: throw UserException.JWTDecodingException("Cannot decode JWT token.")

        // Validate scopes if need
        scope?.let {
            val isScopeValid = it.split(" ").containsAll(requiredScopes!!)
            if (!isScopeValid) throw UserException.ScopeInvalidException("Scope is invalid.")
        }

        val userResponse = service.getUser(userId)
        val loginUser = mapResponseToUser(userResponse)

        _currentUser = loginUser
        _currentLoginCertificate = loginCert

        cachedUsers[userId] = loginUser
        emit(ResourceState.Success(loginCert))
    }.catch { exception ->
        _currentUser = null
        _currentLoginCertificate = null

        Log.e(TAG, exception.message, exception)

        val errorResId = if (exception is UserException.ScopeInvalidException)
            R.string.error_scope_invalid else null
        emit(ResourceState.Error(message = exception.message, resId = errorResId))
    }

    suspend fun register(
        username: String,
        password: String,
        firstName: String,
        lastName: String,
        email: String,
        gender: String,
        birthDate: String
    ): Flow<ResourceState<String>> = flow<ResourceState<String>> {
        val registerResponse = service.register(
            username = username,
            password = password,
            firstName = firstName,
            lastName = lastName,
            email = email,
            gender = gender,
            birthDate = birthDate
        )

        emit(ResourceState.Success(registerResponse))
    }.catch { exception ->
        Log.e(TAG, exception.message, exception)
        emit(ResourceState.Error(message = exception.message))
    }

    /**
     * Get user information from server.
     *
     * Note: User information will be cached in memory.
     */
    fun getUser(userId: String, skipCached: Boolean = false): Flow<ResourceState<User>> {
        if (!skipCached && cachedUsers.containsKey(userId))
            return flowOf(ResourceState.Success(cachedUsers[userId]!!))

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

    private fun mapResponseToUser(userResponse: UserResponse): User = User(
        id = userResponse.id,
        firstName = userResponse.firstName,
        lastName = userResponse.firstName,
        email = userResponse.email
    )

    fun getAllDomains(): Flow<ResourceState<List<Domain>>> = flow<ResourceState<List<Domain>>> {
        val domainList = service.getAllDomains()
            .map(this@UserRepository::mapResponseToDomain)
        emit(ResourceState.Success(domainList))
    }.catch {
        Log.e(TAG, it.message, it)
        emit(ResourceState.Error(message = "Cannot get domain"))
    }

    private fun mapResponseToDomain(response: DomainResponse): Domain = Domain(
        id = response.id,
        name = response.name,
        description = response.description
    )

    companion object {
        const val TAG = "UserRepository"
    }

}