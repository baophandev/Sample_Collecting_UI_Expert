package com.application.android.user_library.datasource.impl

import com.application.android.user_library.datasource.IUserService
import com.application.android.user_library.entity.request.UserLoginRequest
import com.application.android.user_library.entity.request.UserRegisterRequest
import com.application.android.user_library.entity.response.DomainResponse
import com.application.android.user_library.entity.response.LoginResponse
import com.application.android.user_library.entity.response.UserResponse
import com.application.data.datasource.AbstractClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

/**
 * @property baseUrl the base url of the server
 */
class UserServiceImpl(
    private val baseUrl: String
) : IUserService, AbstractClient() {

    private val client = getClient(baseUrl)

    override suspend fun getUser(userId: String): UserResponse {
        return client.get(urlString = userId)
            .body()
    }

    override suspend fun login(username: String, password: String): LoginResponse {
        return client
            .post(urlString = "auth/login") {
                contentType(ContentType.Application.Json)
                setBody(UserLoginRequest(username, password))
            }.body()
    }

    override suspend fun register(
        username: String,
        password: String,
        firstName: String,
        lastName: String,
        email: String,
        gender: String,
        birthDate: String
    ): String {
        val body = UserRegisterRequest(
            username = username,
            password = password,
            firstName = firstName,
            lastName = lastName,
            email = email,
            gender = gender,
            birthdate = birthDate
        )

        return client
            .post(urlString = "auth/register") {
                contentType(ContentType.Application.Json)
                setBody(body)
            }.body()
    }

    override suspend fun getAllDomains(pageNumber: Int, pageSize: Int): List<DomainResponse> {
        return client
            .get(urlString = "domain") {
                url {
                    encodedParameters.append("pageNumber", "$pageNumber")
                    encodedParameters.append("pageSize", "$pageSize")
                }
            }.body()

    }

}