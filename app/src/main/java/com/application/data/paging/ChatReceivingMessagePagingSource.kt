package com.application.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.application.chat_library.data.datasource.IChatService
import com.application.chat_library.data.entity.ChatReceivingMessage
import com.application.chat_library.data.entity.response.ChatMessageResponse
import java.sql.Timestamp

class ChatReceivingMessagePagingSource(
    private val chatService: IChatService,
    private val conversationId: Long,
) : PagingSource<Int, ChatReceivingMessage>() {

    override fun getRefreshKey(state: PagingState<Int, ChatReceivingMessage>): Int? {
        // We need to get the previous key (or next key if previous is null) of the page
        // that was closest to the most recently accessed index.
        // Anchor position is the most recently accessed index
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ChatReceivingMessage> {
        val nextPageNumber = params.key ?: 0
        val response = chatService.getAllMessages(
            conversationId = conversationId,
            pageNumber = nextPageNumber,
            pageSize = params.loadSize
        )

        val messages = response.content.map { mapResponseToMessage(it) }
        val nextKey = if (!response.last) nextPageNumber + 1 else null
        return LoadResult.Page(
            data = messages,
            prevKey = null, // Only paging forward.
            nextKey = nextKey
        )
    }

    private fun mapResponseToMessage(response: ChatMessageResponse): ChatReceivingMessage {
        return ChatReceivingMessage(
            id = response.id,
            type = response.type,
            text = response.text,
            createdAt = Timestamp(response.createdAt),
            senderId = response.senderId
        )
    }

    companion object {
        const val TAG = "ChatMessagePagingSource"
    }

}