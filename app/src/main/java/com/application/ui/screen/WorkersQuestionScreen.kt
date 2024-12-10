package com.application.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.application.R
import com.application.ui.component.BotNavigationBar
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
fun GeneratePostsInWorkersQuestionScreen(navigateToPostAnswerScreen: () -> Unit) {

}

@Composable
fun WorkerQuestionScreenTopBar() {
    var value by remember {
        mutableStateOf("")
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .height(65.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        BasicTextField(
            modifier = Modifier
                .background(
                    color = colorResource(id = R.color.main_green),
                    shape = RoundedCornerShape(30.dp)
                )
                .fillMaxWidth(.8f)
                .fillMaxHeight(.5f),
            value = value,
            onValueChange = { newText ->
                value = newText
            },
            textStyle = TextStyle(
                textAlign = TextAlign.Start,
                color = colorResource(id = R.color.white)
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text
            ),
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier.padding(start = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        Modifier.weight(1f),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (value.isEmpty()) {
                            Text(
                                text = stringResource(id = R.string.search_title),
                                style = LocalTextStyle.current.copy(
                                    color = colorResource(id = R.color.white),
                                    fontSize = 14.sp
                                )
                            )
                        }
                        innerTextField()
                    }
                }
            },
        )
        Column {
            IconButton(
                onClick = { /*TODO*/ },
                modifier = Modifier.size(50.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.notify_icon),
                    contentDescription = "notify",
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                    modifier = Modifier.fillMaxSize(.5f),
                )
            }
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
fun CardTemplateInWorkerQuestionScreen(
    imgRes: List<Int>,
    userName: String,
    postText: String,
    resolved: Boolean,
    onClick: () -> Unit
) {
    val questionString = truncateString(postText)
    Card(
        colors = CardColors(Color.White, Color.White, Color.White, Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
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
                                .fillMaxHeight()
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
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = questionString,
                            modifier = Modifier
                                .padding(start = 12.dp, top = 5.dp),
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
fun WorkersQuestionScreen(
    navigateToHome: (String?) -> Unit,
    navigateToWorkersQuestionScreen: () -> Unit,
    navigateToExpertChatScreen: () -> Unit,
    navigateToPostAnswerScreen: () -> Unit
) {
    val postsWorkersQuestionScreen = listOf(
        PostInWorkersQuestionScreen(
            listOf(
                R.drawable.ic_launcher_background,
                R.drawable.ic_launcher_background,
                R.drawable.ic_launcher_background,
                R.drawable.ic_launcher_background,
                R.drawable.ic_launcher_background,
                R.drawable.ic_launcher_background,
                R.drawable.ic_launcher_background,
                R.drawable.ic_launcher_background,
                R.drawable.ic_launcher_background,
            ),
            "Nông dân X",
            "Tên mẫu one of Asia’s most popular travel destinations, has been badly git a a really long string to test the line",
            true,
        ),
        PostInWorkersQuestionScreen(
            listOf(
                R.drawable.ic_launcher_background,
                R.drawable.ic_launcher_background,
                R.drawable.ic_launcher_background,
            ),
            "Nông dân X",
            "Tên mẫu one of Asia’s most popular travel destinations, has been badly git",
            false,
        ),
    )

    Scaffold(
        topBar = { WorkerQuestionScreenTopBar() },
        bottomBar = {
            BotNavigationBar(
                onWorkersQuestionClick = navigateToWorkersQuestionScreen,
                onExpertChatsClick = navigateToExpertChatScreen
            ) {
                IconButton(
                    modifier = Modifier.size(50.dp),
                    onClick = { navigateToHome(null) }
                ) {
                    Icon(
                        modifier = Modifier.fillMaxSize(.75f),
                        painter = painterResource(id = R.drawable.ic_home),
                        contentDescription = "Home",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }

    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
//                .height(750.dp)
                .padding(innerPadding)
                .background(Color.White),
            content = {
                items(postsWorkersQuestionScreen) { postInformation ->
                    CardTemplateInWorkerQuestionScreen(
                        imgRes = postInformation.imgRes,
                        userName = postInformation.userName,
                        postText = postInformation.postText,
                        resolved = postInformation.resolved,
                        onClick = navigateToPostAnswerScreen
                    )
                }
            }
        )
    }

}

