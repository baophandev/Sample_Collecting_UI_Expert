package com.application.android.utility.client

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.engine.okhttp.OkHttpConfig
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

abstract class AbstractClient {

    /**
     * Creating a new configured HttpClient.
     */
    protected fun getClient(
        baseUrl: String,
        engineConfig: (OkHttpConfig.() -> Unit)? = null
    ): HttpClient {
        return HttpClient(OkHttp) {
            engineConfig?.let { engine(it) }
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
                retryOnServerErrors(maxRetries = 3)
                exponentialDelay()
            }
            install(ContentNegotiation) {
                gson()
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 5000
            }
        }
    }

}