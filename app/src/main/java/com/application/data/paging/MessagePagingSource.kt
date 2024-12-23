package com.application.data.paging

import com.sc.library.chat.data.entity.ReceivingMessage
import com.sc.library.chat.data.repository.MessageRepository

class MessagePagingSource(
    private val conversationId: Long,
    private val messageRepository: MessageRepository
) : AbstractPagingSource<ReceivingMessage>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ReceivingMessage> {
        val nextPageNumber = params.key ?: 0
        val result = messageRepository.getAllMessages(
            conversationId = conversationId,
            pageNumber = nextPageNumber,
            pageSize = params.loadSize
        )
        return retrievePagingForward(nextPageNumber, result)
    }

    companion object {
        const val TAG = "MessagePagingSource"
    }

}