package com.application.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.application.R
import com.application.ui.component.ImageGallery

//Mỗi post gồm List hình ảnh, tên người dùng, văn bản, đã được trả lời chưa
//Display hình ảnh tự động cập nhật UI dựa theo giao diện người dùng
//Nếu văn bản quá dài, hàm truncate sẽ gọt text lại còn trong khoảng 100 char
//IconButton hiển thị khi tham số resolved = true

//Load dl tại hàm GeneratePostsInWorkersQuestionScreen

data class PostInWorkersQuestionScreen(
    val imgRes: List<Int>,
    val userName: String,
    val postText: String,
    val resolved: Boolean,
)

@Composable
fun GeneratePostsInWorkersQuestionScreen(){
    val postsWorkersQuestionScreen = listOf(
        PostInWorkersQuestionScreen(
            listOf(R.drawable.ic_launcher_background,
                R.drawable.ic_launcher_background,
                R.drawable.ic_launcher_background,
                R.drawable.ic_launcher_background,
                R.drawable.ic_launcher_background,
                R.drawable.ic_launcher_background,
                R.drawable.ic_launcher_background,
                R.drawable.ic_launcher_background,
                R.drawable.ic_launcher_background,),
            "Nông dân X",
            "Tên mẫu one of Asia’s most popular travel destinations, has been badly git a a really long string to test the line",
            true,
        ),
        PostInWorkersQuestionScreen(
            listOf(R.drawable.ic_launcher_background,
                R.drawable.ic_launcher_background,
                R.drawable.ic_launcher_background,),
            "Nông dân X",
            "Tên mẫu one of Asia’s most popular travel destinations, has been badly git",
            false,
        ),
    )
    PostsInWorkersQuestionScreen(postsWorkersQuestionScreen)
}

@Composable
fun WorkerQuestionScreenTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(top = 8.dp, start = 12.dp, end = 12.dp, bottom = 29.dp)
    ) {
        Column {
            IconButton(
                onClick = { /*TODO*/ },
                colors = IconButtonColors(
                    Color(0xFF007E2F),
                    Color(0xFF007E2F),
                    Color(0xFF007E2F),
                    Color(0xFF007E2F)
                ),
                modifier = Modifier
                    .height(45.dp)
                    .width(100.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.return_arrow),
                    contentDescription = "view answer",
                    contentScale = ContentScale.Crop,
                    colorFilter = ColorFilter.tint(Color.White),
                    modifier = Modifier
                        .size(20.dp)
                )
            }
        }
        Column(
            modifier = Modifier
                .padding(start = 5.dp)
        ) {
            OutlinedTextField(
                value = "",
                onValueChange = { /*TODO*/ },
                singleLine = true,
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors().copy(
                    unfocusedContainerColor = Color(0xFF007E2F),
                    focusedContainerColor = Color(0xFF007E2F)
                ),
                placeholder = {
                    Box(
                        modifier = Modifier
                            .padding(
                                start = 20.dp,
                                end = 10.dp,
                            )
                    ) {
                        Text(
                            text = "Tìm kiếm...",
                            color = Color.White,
                            style = TextStyle(
                                fontSize = 11.8.sp,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 28.sp,
                            ),
                        )
                    }
                },

                modifier = Modifier
                    .width(330.dp)
                    .height(45.dp)
                    .border(3.dp, Color(0xFF007E2F), RoundedCornerShape(20.dp))
            )
        }
    }
}

fun truncateString(s: String): String {
    if (s.length <= 100) {
        return s
    }

    val words = s.split(" ")
    var result = ""
    var length = 0

    for (word in words) {
        if (length + word.length + 1 > 100) {
            result = result.trim() + "..."
            break
        }
        result += "$word "
        length += word.length + 1
    }

    return result.trim()
}

@Composable
fun CardTemplateInWorkerQuestionScreen(imgRes: List<Int>, userName: String, postText: String, resolved: Boolean) {
    val questionString = truncateString(postText)
    Card(
        colors = CardColors(Color.White, Color.White, Color.White, Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 3.dp)
            .height(305.dp)
            .padding(start = 12.dp, end = 12.dp, bottom = 20.dp)
            .shadow(5.dp, shape = RoundedCornerShape(13.dp), false, Color.Black)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 12.dp, end = 12.dp, top = 9.dp)
        ) {
            //Prototype Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(187.dp)
            ) {
                ImageGallery(images = imgRes, maxWidth = 352, maxHeight = 187)
            }

            Column {
                if (resolved) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(22.dp)
                    ) {
                        Text(
                            text = userName,
                            modifier = Modifier
                                .padding(start = 12.dp, end = 12.dp, top = 5.dp),
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Black
                            ),
                        )
                    }
                    Row {
                        Column(
                            modifier = Modifier
                                .width(300.dp)
                                .fillMaxHeight()
                                .padding(bottom = 7.dp)
                        ) {
                            Text(
                                text = questionString,
                                modifier = Modifier
                                    .padding(start = 12.dp, end = 12.dp, top = 5.dp),
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    color = Color(0xFF625B71)
                                ),
                            )
                        }
                        Column(
                            modifier = Modifier
                                .fillMaxHeight()
                                .padding(top = 5.dp)
                        ) {
                            IconButton(
                                onClick = { /*TODO*/ },
                                colors = IconButtonColors(
                                    Color(0xFF7CC6FF),
                                    Color(0xFF7CC6FF),
                                    Color(0xFF7CC6FF),
                                    Color(0xFF7CC6FF)
                                ),
                                modifier = Modifier.size(45.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.chat_bubble),
                                    contentDescription = "view answer",
                                    contentScale = ContentScale.Crop,
                                    colorFilter = ColorFilter.tint(Color.White),
                                    modifier = Modifier
                                        .size(24.dp)
                                )
                            }
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(22.dp)
                    ) {
                        Text(
                            text = "Nông dân X",
                            modifier = Modifier
                                .padding(start = 12.dp, end = 12.dp, top = 5.dp),
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Black
                            ),
                        )
                    }
                    Row (
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = questionString,
                            modifier = Modifier
                                .padding(start = 12.dp, end = 12.dp, top = 5.dp, bottom = 7.dp),
                            style = TextStyle(
                                fontSize = 14.sp,
                                color = Color(0xFF625B71)
                            ),
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PostsInWorkersQuestionScreen(postsWorkersQuestionScreenInformation: List<PostInWorkersQuestionScreen>){
    LazyColumn(
        modifier = Modifier
            .height(750.dp)
            .background(Color.White),
        content = {
            items(postsWorkersQuestionScreenInformation) { postInformation ->
                CardTemplateInWorkerQuestionScreen(
                    imgRes = postInformation.imgRes,
                    userName = postInformation.userName,
                    postText = postInformation.postText,
                    resolved = postInformation.resolved,
                )
            }
        }
    )
}

@Composable
fun WorkersQuestionScreen() {
    Scaffold(
        topBar = { WorkerQuestionScreenTopBar() }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            GeneratePostsInWorkersQuestionScreen()
        }

//        Text(text = "HI", modifier = Modifier.padding(innerPadding))
    }

}

@Preview(heightDp = 800, widthDp = 400, showBackground = true)
@Composable
fun WorkersQuestionScreenPreview() {
    WorkersQuestionScreen()
}