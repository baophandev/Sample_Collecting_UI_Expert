package com.application.chat_library.data.datasource.impl

import android.util.Log
import com.application.android.utility.client.AbstractClient
import com.application.chat_library.data.datasource.IChatService
import com.application.chat_library.data.entity.ChatSendingMessage
import com.application.chat_library.data.entity.request.ChatParticipantCreateRequest
import com.application.chat_library.data.entity.response.ChatConversationCreateRequest
import com.application.chat_library.data.entity.response.ChatConversationResponse
import com.application.chat_library.data.entity.response.ChatMessageResponse
import com.application.chat_library.data.entity.response.ChatNotificationResponse
import com.application.chat_library.data.entity.response.ChatWebSocketResponse
import com.application.data.entity.response.PagingResponse
import com.google.gson.Gson
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpStatusCode
import io.reactivex.Completable
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.runBlocking
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient

class ChatServiceImpl(
    private val baseUrl: String
) : IChatService, AbstractClient() {

//    private val client = getClient("http://10.0.2.2:8081/api/v1/chat/")
    private val client = getClient(baseUrl)

    private var stompClient: StompClient
    private var publishDest: String
    private var subscribeConversationDest: String
    private var subscribeNotificationDest: String

    private val conversationIdReplacer = Regex("\\{conversationId\\}")
    private val userIdReplacer = Regex("\\{userId\\}")

    init {
        runBlocking {
            val information = getWebSocketServer()
            val connectUri = "ws://${information.serverHost}:${information.serverPort}/chat"

            stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, connectUri)
            publishDest = information.publishDest
            subscribeConversationDest = information.subscribeConversationDest
            subscribeNotificationDest = information.subscribeNotificationDest
        }
    }

    override suspend fun getAllConversations(
        userId: String,
        pageNumber: Int,
        pageSize: Int
    ): PagingResponse<ChatConversationResponse> {
        return client.get(urlString = "conversation/$userId/user") {
            url {
                encodedParameters.append("pageNumber", "$pageNumber")
                encodedParameters.append("pageSize", "$pageSize")
            }
        }.body()
    }

    override suspend fun getConversation(conversationId: Long): ChatConversationResponse {
        return client.get(urlString = "conversation/$conversationId")
            .body()
    }

    override suspend fun createConversation(body: ChatConversationCreateRequest): Long {
        return client.post(urlString = "conversation") {
            setBody(body)
        }.body()
    }

    override suspend fun deleteConversation(conversationId: Long): Boolean {
        return client.delete(urlString = "conversation/$conversationId")
            .status == HttpStatusCode.NoContent
    }

    override suspend fun getAllMessages(
        conversationId: Long,
        pageNumber: Int,
        pageSize: Int
    ): PagingResponse<ChatMessageResponse> {
        return client.get(urlString = "message/$conversationId") {
            url {
                encodedParameters.append("pageNumber", "$pageNumber")
                encodedParameters.append("pageSize", "$pageSize")
            }
        }.body()
    }

    override suspend fun deleteMessage(messageId: Long): Boolean {
        return client.delete(urlString = "message/$messageId")
            .status == HttpStatusCode.NoContent
    }

    override suspend fun getAllParticipants(
        conversationId: Long,
        pageNumber: Int,
        pageSize: Int
    ): PagingResponse<ChatMessageResponse> {
        return client.get(urlString = "participant/$conversationId") {
            url {
                encodedParameters.append("pageNumber", "$pageNumber")
                encodedParameters.append("pageSize", "$pageSize")
            }
        }.body()
    }

    override suspend fun addParticipant(body: ChatParticipantCreateRequest): Long {
        return client.post(urlString = "participant") {
            setBody(body)
        }.body()
    }

    override suspend fun getWebSocketServer(): ChatWebSocketResponse {
        return client.get(urlString = "server")
            .body()
    }

    override fun connectWebSocketServer(userId: String): Completable {
        if (!stompClient.isConnected) Log.d(TAG, "Connected to WebSocket.")
        stompClient.connect()
        return sendMessage("/sc/user/$userId/connected")
    }

    override fun disconnectWebSocketServer(userId: String) {
        if (!stompClient.isConnected) return

        stompClient
            .send("/sc/user/$userId/disconnected")
            .retry(2)
            .onErrorComplete {
                Log.e(TAG, it.message ?: "Unknown exception")
                true
            }
            .doFinally {
                stompClient.disconnect()
            }
            .blockingAwait()
    }

    override fun sendMessage(
        destinationPath: String,
        payload: String?,
        retryTimes: Long,
        errorHandler: ((Throwable) -> Unit)?
    ): Completable {
        Log.i(TAG, "Send message to: $destinationPath\nPayload: $payload")
        Log.i(TAG, "STOMP Client: $stompClient")

        return stompClient
            .send(destinationPath, payload)
            .retry(retryTimes)
            .onErrorComplete { exception ->
                Log.e(TAG, exception.message ?: "Unknown exception")
                errorHandler?.let { errorHandler(exception) }
                false
            }
    }

    override fun sendMessage(
        conversationId: Long,
        message: ChatSendingMessage,
    ): Completable {
        val payload = Gson().toJson(message)
        val destinationPath = publishDest
            .replace(conversationIdReplacer, "$conversationId")
        return sendMessage(destinationPath, payload)
    }

    override fun subscribeConversation(
        conversationId: Long,
        handler: (ChatMessageResponse) -> Unit
    ): Disposable? {
        val destinationPath = subscribeConversationDest
            .replace(conversationIdReplacer, "$conversationId")
        return subscribeTopic(destinationPath, ChatMessageResponse::class.java, handler)
    }

    override fun subscribeNotification(
        userId: String,
        handler: (ChatNotificationResponse) -> Unit
    ): Disposable? {
        val destinationPath = subscribeNotificationDest
            .replace(userIdReplacer, userId)
        return subscribeTopic(destinationPath, ChatNotificationResponse::class.java, handler)
    }

    override fun <T> subscribeTopic(
        destinationPath: String,
        classOfT: Class<T>,
        handler: (T) -> Unit
    ): Disposable? {
        return stompClient.topic(destinationPath)
            .subscribe { stompMessage ->
                val messageResponse =
                    Gson().fromJson(stompMessage.payload, classOfT)
                classOfT.cast(messageResponse)?.let(handler)
            }
    }

    companion object {
        const val TAG = "ChatServiceImpl"
    }

}