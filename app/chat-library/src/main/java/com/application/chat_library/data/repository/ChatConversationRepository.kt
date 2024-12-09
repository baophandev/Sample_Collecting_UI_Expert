package com.application.chat_library.data.repository

import android.util.Log
import com.application.android.utility.state.ResourceState
import com.application.chat_library.constant.ChatConversationType
import com.application.constant.ChatParticipantType
import com.application.chat_library.data.datasource.IChatService
import com.application.chat_library.data.entity.ChatReceivingMessage
import com.application.chat_library.data.entity.ChatSendingMessage
import com.application.chat_library.data.entity.request.ChatParticipantCreateRequest
import com.application.chat_library.data.entity.response.ChatConversationCreateRequest
import com.application.chat_library.data.entity.response.ChatNotificationResponse
import io.reactivex.Completable
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import java.sql.Timestamp

class ChatConversationRepository(
    private val service: IChatService,
) {
    private var notificationSubscriber: Disposable? = null
    private val registeredConversations = mutableMapOf<Long, Disposable?>()

    fun createConversation(
        title: String,
        type: ChatConversationType = ChatConversationType.SINGLE,
        creatorId: String,
        participantId: String
    ): Flow<ResourceState<Long>> {
        val createRequest = ChatConversationCreateRequest(title, type, creatorId)
        return flow<ResourceState<Long>> {
            val conversationId = service.createConversation(createRequest)
            service.addParticipant(
                ChatParticipantCreateRequest(
                    participantId,
                    ChatParticipantType.MEMBER,
                    conversationId
                )
            )
            emit(ResourceState.Success(conversationId))
        }.catch {
            Log.e(TAG, it.message, it)
            emit(ResourceState.Error(message = "Cannot create a new conversation"))
        }
    }

    /**
     * Connect to Chat Server and register a notification handler.
     */
    fun connectChatServer(userId: String): Completable {
        return service.connectWebSocketServer(userId)
    }

    fun disconnectChatServer(userId: String) {
        registeredConversations.keys.forEach(this::unregisterMessageHandler)
        unregisterNotificationHandler()
        service.disconnectWebSocketServer(userId)
    }

    /**
     * Send a new message to server via WebSocket.
     */
    fun sendMessage(
        conversationId: Long,
        message: ChatSendingMessage,
    ): Completable {
        return service.sendMessage(conversationId, message)
    }

    /**
     * Register an incoming message handler which will be called whenever having a new message.
     * In addition, this method is going to initialize a new client to configure and connect to WebSocket.
     */
    fun registerMessageHandler(conversationId: Long, handler: (ChatReceivingMessage) -> Unit) {
        if (registeredConversations.containsKey(conversationId)) return

        Log.d(TAG, "Registered message handler for conversation ID: $conversationId.")
        val disposable = service.subscribeConversation(conversationId) {
            val incomingMessage = ChatReceivingMessage(
                id = it.id,
                type = it.type,
                text = it.text,
                createdAt = Timestamp(it.createdAt),
                senderId = it.senderId
            )
            handler(incomingMessage)
        }
        registeredConversations[conversationId] = disposable
    }

    private fun unregisterMessageHandler(conversationId: Long) {
        if (!registeredConversations.containsKey(conversationId)) return
        val disposable = registeredConversations[conversationId]
        if (disposable?.isDisposed == false) disposable.dispose()
    }

    fun registerNotificationHandler(
        userId: String,
        handler: (ChatNotificationResponse) -> Unit
    ) {
        if (notificationSubscriber != null) return
        val disposable = service.subscribeNotification(userId, handler)
        notificationSubscriber = disposable
    }

    private fun unregisterNotificationHandler() {
        notificationSubscriber?.dispose()
    }

    companion object {
        const val TAG = "ChatConversationRepository"
    }
}