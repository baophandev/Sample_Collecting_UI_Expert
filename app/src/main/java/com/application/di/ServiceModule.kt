package com.application.di

import com.application.constant.ServiceHost
import com.application.data.datasource.IPostService
import com.application.data.datasource.IProjectService
import com.application.data.datasource.impl.PostServiceImpl
import com.application.data.datasource.impl.ProjectServiceImpl
import io.github.nhatbangle.sc.attachment.datasource.IAttachmentService
import io.github.nhatbangle.sc.attachment.datasource.impl.AttachmentServiceImpl
import io.github.nhatbangle.sc.chat.data.datasource.IChatService
import io.github.nhatbangle.sc.user.datasource.IUserService
import io.github.nhatbangle.sc.chat.data.datasource.impl.ChatServiceImpl
import io.github.nhatbangle.sc.user.datasource.impl.UserServiceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.Charsets
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.HttpHeaders
import io.ktor.serialization.gson.gson
import io.ktor.util.appendIfNameAbsent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {

    @Provides
    @Singleton
    fun provideHttpClient() : HttpClient {
        val baseUrl = "http://${ServiceHost.GATEWAY_SERVER}/"
        return HttpClient(OkHttp) {
            Charsets {
                register(Charsets.UTF_8)
                sendCharset = Charsets.UTF_8
            }
            expectSuccess = true
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.ALL
                sanitizeHeader { header -> header == HttpHeaders.Authorization }
            }
            install(DefaultRequest) {
                url(baseUrl)
                headers.appendIfNameAbsent(HttpHeaders.ContentType, "application/json")
            }
            install(HttpRequestRetry) {
                retryOnServerErrors(maxRetries = maxRetries)
                exponentialDelay()
            }
            install(ContentNegotiation) {
                gson()
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 50000
            }
        }
    }

    @Provides
    @Singleton
    fun provideUserService(client: HttpClient) : IUserService {
        val prefixPath = "api/v1/user"
        return UserServiceImpl(client, prefixPath)
    }

    @Provides
    @Singleton
    fun provideProjectService(client: HttpClient) : IProjectService {
        val prefixPath = "api/v1"
        return ProjectServiceImpl(client, prefixPath)
    }

    @Provides
    @Singleton
    fun provideChatService(client: HttpClient) : IChatService {
        val prefixPath = "api/v1/chat"
        return ChatServiceImpl(client, prefixPath)
    }

    @Provides
    @Singleton
    fun providePostService(client: HttpClient) : IPostService {
        val prefixPath = "api"
        return PostServiceImpl(client, prefixPath)
    }

    @Provides
    @Singleton
    fun provideAttachmentService(client: HttpClient) : IAttachmentService {
        val prefixPath = "api/file"
        return AttachmentServiceImpl(client, prefixPath)
    }

}