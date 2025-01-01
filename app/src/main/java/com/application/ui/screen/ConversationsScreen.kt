package com.application.ui.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.application.R
import com.application.ui.component.ConversationBar
import com.application.ui.component.ExpertChatTopNavigationBar
import com.application.ui.component.PagingLayout
import com.application.ui.viewmodel.ConversationsViewModel
import io.github.nhatbangle.sc.chat.constant.MessageType
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@Composable
fun ConversationsScreen(
    viewModel: ConversationsViewModel = hiltViewModel(),
    navigateToHome: () -> Unit,
    navigateToChat: (Long) -> Unit,
) {
    val convFlow = viewModel.conversationsFlow.collectAsLazyPagingItems()

    Scaffold(
        topBar = { ExpertChatTopNavigationBar(route = navigateToHome) }
    ) { innerPadding ->
        PagingLayout(
            modifier = Modifier.padding(innerPadding),
            pagingItems = convFlow,
            itemsContent = { conversation ->
                val updatedAt = DateTimeFormatter.ofPattern("dd/MM/yy H:mma")
                    .withZone(ZoneOffset.UTC)
                    .format(conversation.updatedAt.toInstant())
                val lastMessage = conversation.lastMessage?.let {
                    when (it.type) {
                        MessageType.TEXT -> it.text
                        else -> "[${stringResource(R.string.file)}]"
                    }
                } ?: ""

                ConversationBar(
                    title = conversation.title,
                    lastMessage = lastMessage,
                    updatedAt = updatedAt,
                    read = false,
//                    read = conversation.isRead,
                    onClick = { navigateToChat(conversation.id) }
                )
            },
            itemKey = convFlow.itemKey { it.id },
            noItemContent = {
                Text(
                    text = stringResource(id = R.string.no_conversation),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        )
    }
}