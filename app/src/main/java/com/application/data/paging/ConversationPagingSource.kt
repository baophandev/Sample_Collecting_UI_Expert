package com.application.data.paging

import io.github.nhatbangle.sc.chat.data.entity.Conversation
import io.github.nhatbangle.sc.chat.data.repository.ConversationRepository
import io.github.nhatbangle.sc.user.repository.UserRepository

class ConversationPagingSource(
    private val userRepository: UserRepository,
    private val conversationRepository: ConversationRepository
) : AbstractPagingSource<Conversation>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Conversation> {
        val userId = userRepository.loggedUser?.id
            ?: return LoadResult.Error(Exception("User not logged in."))

        val nextPageNumber = params.key ?: 0
        val result = conversationRepository.getAllConversations(
            userId = userId,
            pageNumber = nextPageNumber,
            pageSize = params.loadSize
        )
        return retrievePagingForward(nextPageNumber, result)
    }

    companion object {
        const val TAG = "ConversationPagingSource"
    }

}