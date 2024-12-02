package com.application.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.application.chat_library.data.datasource.IChatService
import com.application.chat_library.data.entity.ChatConversation
import com.application.chat_library.data.entity.response.ChatConversationResponse
import com.application.data.repository.UserRepository
import java.sql.Timestamp

class ChatConversationPagingSource(
    private val chatService: IChatService,
    private val userRepository: UserRepository
) : PagingSource<Int, ChatConversation>() {

    override fun getRefreshKey(state: PagingState<Int, ChatConversation>): Int? {
        // We need to get the previous key (or next key if previous is null) of the page
        // that was closest to the most recently accessed index.
        // Anchor position is the most recently accessed index
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ChatConversation> {
        val nextPageNumber = params.key ?: 0
        val user = userRepository.getLoggedUser()
            ?: return LoadResult.Error(Exception("Cannot get conversations because of user not logged in"))
        val response = chatService.getAllConversations(
            userId = user.id,
            pageNumber = nextPageNumber,
            pageSize = params.loadSize
        )

        val conversations = response.content.map(this::mapResponseToConversation)
        val nextKey = if (!response.last) nextPageNumber + 1 else null
        return LoadResult.Page(
            data = conversations,
            prevKey = null, // Only paging forward.
            nextKey = nextKey
        )
    }

    private fun mapResponseToConversation(response: ChatConversationResponse): ChatConversation {
        return ChatConversation(
            id = response.id,
            title = response.title,
            creatorId = response.creatorId,
            updatedAt = Timestamp(response.updatedAt)
        )
    }

    companion object {
        const val TAG = "ChatConversationPagingSource"
    }

}