package com.application.data.paging

import io.github.nhatbangle.sc.chat.data.entity.ReceivingMessage
import io.github.nhatbangle.sc.chat.data.repository.MessageRepository

class MessagePagingSource(
    private val conversationId: Long,
    private val messageRepository: MessageRepository
) : AbstractPagingSource<ReceivingMessage>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ReceivingMessage> {
        val nextPageNumber = params.key ?: 0
        return messageRepository.getAllMessages(
            conversationId = conversationId,
            pageNumber = nextPageNumber,
            pageSize = params.loadSize
        ).map {
            val nextKey = if (!it.last) nextPageNumber + 1 else null
            val data = it.content

            return LoadResult.Page(
                data = data,
                prevKey = null, // Only paging forward but reverse direction.
                nextKey = nextKey
            )
        }.getOrElse { LoadResult.Error(it) }
    }

    companion object {
        const val TAG = "MessagePagingSource"
    }

}