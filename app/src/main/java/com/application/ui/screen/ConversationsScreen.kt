package com.application.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.application.R
import com.application.ui.component.ExpertChatTopNavigationBar
import com.application.ui.component.UserConversationBar

data class UserDataConversation(
    val userAvatar: Int,
    val userName: String,
    val userLastMessage: String,
    val messageSentTime: String,
    val read: Boolean,
)

@Composable
fun ConversationsScreen(
    navigateToHome: () -> Unit,
    navigateToChat: () -> Unit,
) {
    val conversations = listOf(
        UserDataConversation(
            R.drawable.ic_launcher_background,
            "Baro",
            "Tối thứ 3 họp",
            "1 phút trước",
            true
        ),
        UserDataConversation(
            R.drawable.ic_launcher_background,
            "B",
            "Call qua API này",
            "1 ngày trước",
            false
        ),
        UserDataConversation(
            R.drawable.ic_launcher_background,
            "Y",
            "Có lỗi chính tả chỗ",
            "2 tháng trước",
            true
        ),
        UserDataConversation(
            R.drawable.ic_launcher_background,
            "K with a long name",
            "Cái này Bằng làm, có gì hỏi",
            "3 giây trước",
            false
        ),
        UserDataConversation(
            R.drawable.ic_launcher_background,
            "Just Hai",
            ":3",
            "2 năm trước",
            false
        ),
    )

    Scaffold(
        topBar = {
            ExpertChatTopNavigationBar(route = navigateToHome )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .background(Color.White)
                    .height(800.dp)

            ) {
                items(conversations) { conversation ->
                    UserConversationBar(
                        userAvatar = conversation.userAvatar,
                        userName = conversation.userName,
                        userLastMessage = conversation.userLastMessage,
                        messageSentTime = conversation.messageSentTime,
                        read = conversation.read,
                        onUserConversationClick = navigateToChat
                    )
                }
            }
        }
    }
}




