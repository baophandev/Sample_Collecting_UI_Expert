package com.application.ui.component

import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.application.R
import com.application.ui.theme.SampleCollectingApplicationTheme

@Composable
fun ConversationBar(
    userAvatar: Uri? = null,
    read: Boolean,
    title: String,
    lastMessage: String,
    updatedAt: String,
    onClick: () -> Unit
) {
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable(onClick = onClick)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .size(90.dp)
        ) {
            if (userAvatar != null) AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(userAvatar)
                    .error(R.drawable.ic_launcher_background)
                    .build(),
                contentDescription = "view answer",
                contentScale = ContentScale.Inside,
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
            )
            else Image(
                painter = painterResource(R.drawable.ic_launcher_background),
                contentDescription = "view answer",
                contentScale = ContentScale.Inside,
                colorFilter = ColorFilter.tint(Color(0xFF007E2F)),
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
                .padding(5.dp)
        ) {
            Box(
                modifier = Modifier
                    .padding(start = 5.dp)
            ) {
                //Hiển thị tên và thời gian gửi tin nhắn cuối
                Row(
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier
                        .height(37.dp)
                        .fillMaxWidth()
                        .padding(start = 5.dp)
                ) {
                    Text(
                        text = title,
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = if (read) FontWeight.Medium else FontWeight.ExtraBold
                        ),
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        modifier = Modifier
                            .fillMaxWidth(0.68f)
                    )
                    Text(
                        text = updatedAt,
                        style = TextStyle(
                            fontWeight = if (read) FontWeight.Medium else FontWeight.ExtraBold
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 1.5.dp),
                    )
                }
                //Hiển thị tin nhắn cuối và biểu tượng chưa đọc
                Row(
                    modifier = Modifier
                        .padding(top = 40.dp, start = 5.dp)
                ) {
                    Text(
                        text = lastMessage,
                        style = TextStyle(
                            color = if (read) Color.Gray else Color.Black,
                            fontSize = 12.sp,
                        ),
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                    )
                    if (!read) Canvas(
                        modifier = Modifier
                            .padding(top = 3.dp)
                            .size(8.dp)
                    ) {
                        drawCircle(Color(0xFFff5056))
                    }
                }
                HorizontalDivider(
                    color = Color(0xFFb7b7b7),
                    modifier = Modifier
                        .padding(top = 72.dp)
                        .height(2.dp)
                        .fillMaxWidth()
                )
            }
        }
    }
}

@Preview(widthDp = 450)
@Composable
private fun Test() {
    SampleCollectingApplicationTheme {
        Column(Modifier.background(Color.White)) {
            ConversationBar(
                title = "test",
                lastMessage = "1234567",
                updatedAt = "22/12/24 8:07 PM",
                read = true,
                onClick = { }
            )
            ConversationBar(
                title = "test",
                lastMessage = "1234567",
                updatedAt = "22/12/24 8:07 PM",
                read = true,
                onClick = { }
            )
        }
    }
}

