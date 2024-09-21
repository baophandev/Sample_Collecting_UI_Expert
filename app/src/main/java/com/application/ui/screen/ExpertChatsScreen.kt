package com.application.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.application.R
import com.application.ui.component.UserConversationBar

//Mỗi cuộc trò chuyện cần thông tin về người dùng (avatar, name)
//và nội dung trò chuyện (tin nhắn cuối, thời gian gửi, đã đọc chưa)

//Truyền data qua fun GenerateUserDataConversation


data class UserDataConversation(
    val userAvatar: Int,
    val userName: String,
    val userLastMessage: String,
    val messageSentTime: String,
    val read: Boolean,
)

@Composable
private fun GenerateUserDataConversation() {
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
    ExpertChatsScreen(conversations)
}

@Composable
private fun ExpertChatsSearchBar() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF6CCD84))
            .padding(start = 12.dp, top = 8.dp, bottom = 8.dp)
    ) {
        TextField(
            value = "",
            onValueChange = { /*TODO*/ },
            singleLine = true,
            shape = RoundedCornerShape(20.dp),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Search Icon",
                    tint = Color.White,
                    modifier = Modifier
                        .fillMaxHeight()
                )
            },
            colors = OutlinedTextFieldDefaults.colors().copy(
                unfocusedContainerColor = Color(0xFF6CCD84),
                focusedContainerColor = Color(0xFF6CCD84),
                focusedIndicatorColor = Color(0xFF6CCD84),
                unfocusedIndicatorColor = Color(0xFF6CCD84),
            ),
            placeholder = {
                Text(
                    text = "Tìm kiếm...",
                    color = Color.White,
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                    ),
                )
            },
            modifier = Modifier
                .width(330.dp)
                .height(45.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = Color.White,
                    modifier = Modifier
                        .fillMaxHeight()
                )
            }
        }
    }
}

@Composable
fun ExpertChatsScreen(conversations: List<UserDataConversation>) {
    Scaffold(
        topBar = {
            ExpertChatsSearchBar()
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
                    )
                }
            }
        }
    }
}


@Preview(heightDp = 800, widthDp = 400, showBackground = true)
@Composable
fun ExpertChatsScreenPreview() {
    GenerateUserDataConversation()
}


