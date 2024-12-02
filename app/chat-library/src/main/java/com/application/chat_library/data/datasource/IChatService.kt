package com.application.chat_library.data.datasource

import com.application.chat_library.data.entity.request.ChatParticipantCreateRequest
import com.application.chat_library.data.entity.ChatSendingMessage
import com.application.chat_library.data.entity.response.ChatConversationCreateRequest
import com.application.chat_library.data.entity.response.ChatMessageResponse
import com.application.chat_library.data.entity.response.ChatConversationResponse
import com.application.chat_library.data.entity.response.ChatNotificationResponse
import com.application.chat_library.data.entity.response.ChatWebSocketResponse
import com.application.data.entity.response.PagingResponse
import io.reactivex.Completable
import io.reactivex.disposables.Disposable

interface IChatService {

    suspend fun getAllConversations(
        userId: String,
        pageNumber: Int = 0,
        pageSize: Int = 6
    ): PagingResponse<ChatConversationResponse>

    suspend fun getConversation(conversationId: Long): ChatConversationResponse

    suspend fun createConversation(body: ChatConversationCreateRequest): Long

    suspend fun deleteConversation(conversationId: Long): Boolean

    suspend fun getAllMessages(
        conversationId: Long,
        pageNumber: Int = 0,
        pageSize: Int = 6
    ): PagingResponse<ChatMessageResponse>

    suspend fun deleteMessage(messageId: Long): Boolean

    suspend fun getAllParticipants(
        conversationId: Long,
        pageNumber: Int = 0,
        pageSize: Int = 6
    ): PagingResponse<ChatMessageResponse>

    suspend fun addParticipant(body: ChatParticipantCreateRequest): Long

    suspend fun getWebSocketServer() : ChatWebSocketResponse

    /**
     * Initialize a STOMP client which is used for communicating via WebSocket.
     * This method is going to initialize a client and connect to the service server.
     * @param userId ID of a user who is connecting to server.
     */
    fun connectWebSocketServer(userId: String): Completable

    /**
     * @param userId ID of a user who is disconnecting to server.
     */
    fun disconnectWebSocketServer(userId: String)

    fun sendMessage(
        destinationPath: String,
        payload: String? = null,
        retryTimes: Long = 2,
        errorHandler: ((Throwable) -> Unit)? = null
    ): Completable

    /**
     * Send a new message to a conversation.
     */
    fun sendMessage(
        conversationId: Long,
        message: ChatSendingMessage,
    ): Completable

    fun <T> subscribeTopic(
        destinationPath: String,
        classOfT: Class<T>,
        handler: (T) -> Unit
    ): Disposable?

    /**
     * Subscribe to a conversation.
     * If the conversation has a new message, the handler is going to be called.
     */
    fun subscribeConversation(
        conversationId: Long,
        handler: (ChatMessageResponse) -> Unit
    ): Disposable?

    fun subscribeNotification(
        userId: String,
        handler: (ChatNotificationResponse) -> Unit
    ): Disposable?

}