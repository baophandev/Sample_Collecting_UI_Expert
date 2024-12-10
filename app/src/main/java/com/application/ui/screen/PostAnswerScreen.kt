package com.application.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.application.R

//Mỗi dự án là một post. Mỗi post có các hình ảnh (Image) và 1 câu trả lời chung
//Image gồm câu hỏi và description từ khách + câu trả lời và hình ảnh trả lời (professional)

data class PostAnswerData(
    val images: List<ImageAnswerData>,
    val generalAnswer: String? = null,
)

data class ImageAnswerData(
    val image: Int,
    val description: String? = null,
    val answer: String? = null,
    val answerPicture: Int? = null,
)


//allImagesInsideThisOnePost là một mảng hình ảnh (Mỗi hình ảnh gồm hình + mô tả +  của một bài viết
//post là tập hợp tất cả ảnh + 1 câu trả lời chung
@Composable
fun GeneratePostInPostAnswerScreen() {
    //Danh sách từng ảnh
    val allImagesInsideThisOnePost = listOf(
        ImageAnswerData(
            R.drawable.ic_launcher_background, //Ảnh nông sản
            "hehe ma tui muon biet la comment nay hoi dai de chung to no hien len nua dai hon nua ne. van con chua het comment",
            "",
            R.drawable.splash_logo, //Ảnh chuyên gia trả lời
        ),
//        ImageAnswerData(R.drawable.birthday_icon, "hinh nay chup dep ne")
    )

    //Danh sách tất cả các ảnh
    val posts =
        PostAnswerData(
            allImagesInsideThisOnePost,
            "Noi chung la tui khong ro nua"
        )
    ImagesInPostAnswerScreen(posts)
}

//image = -1 nghĩa là không có hình ảnh nào được cấp.
//Phiên bản hiện tại không hiển thị file hay hình ảnh gì cả, nếu cần chỉnh sửa theo hướng review được
//uncomment phần nội dung bên trong hàm LoadEachImage
@Composable
private fun LoadEachImage(image: Int = -1, index: Int) {
//    if (image != -1) {
//        Image(
//            painter = painterResource(id = image),
//            contentDescription = "Image $index",
//            contentScale = ContentScale.FillHeight,
//            modifier = Modifier
//                .fillMaxHeight()
//                .fillMaxSize(0.3f)
//                .clip(RoundedCornerShape(20.dp)),
//        )
//        TextField(
//            value = "",
//            onValueChange = {},
//            shape = RoundedCornerShape(20.dp),
//            colors = TextFieldDefaults.colors(
//                focusedIndicatorColor = Color.Transparent,
//                unfocusedIndicatorColor = Color.Transparent,
//                disabledIndicatorColor = Color.Transparent,
//                errorIndicatorColor = Color.Transparent
//            ),
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(start = 5.dp)
//        )
//    } else {
//        TextField(
//            value = "",
//            onValueChange = {},
//            shape = RoundedCornerShape(20.dp),
//            colors = TextFieldDefaults.colors(
//                focusedIndicatorColor = Color.Transparent,
//                unfocusedIndicatorColor = Color.Transparent,
//                disabledIndicatorColor = Color.Transparent,
//                errorIndicatorColor = Color.Transparent
//            ),
//            modifier = Modifier
//                .fillMaxSize()
//        )
//    }
            TextField(
            value = "",
            onValueChange = {},
            shape = RoundedCornerShape(20.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent
            ),
            modifier = Modifier
                .fillMaxSize()
        )
}

@Composable
private fun GeneralAnswer(){
    Column(
        modifier = Modifier
            .padding(15.dp, 0.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = "Trả lời tổng thể:",
            style = TextStyle(
                color = Color(0xFF7cc6ff),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
            )
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 5.dp)
                .height(230.dp)
                .shadow(5.dp, shape = RoundedCornerShape(13.dp), false, Color.Black)
        ) {
            TextField(
                value = "",
                onValueChange = {},
                shape = RoundedCornerShape(20.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.75f)

//                                .background(Color.Red)
            )
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 3.dp)
                    .height(1.dp)
                    .background(Color.Gray)
            )
            Row(
                modifier = Modifier
                    .padding(top = 12.dp, start = 10.dp)
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f)
            ) {
                IconButton(
                    onClick = { /*TODO*/ },
                    modifier = Modifier
                        .padding(start = 6.dp, end = 10.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_gallery),
                        contentDescription = "",
                        tint = Color.Gray
                    )
                }
                IconButton(
                    onClick = { /*TODO*/ }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_paperclip),
                        contentDescription = "Paperclip",
                        tint = Color.Gray,
                    )
                }
            }
        }

    }
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 12.dp, end = 12.dp, top = 10.dp)
    ) {
    }
}

@Composable
fun ImagesInPostAnswerScreen(posts: PostAnswerData) {
    LazyColumn(
        modifier = Modifier
            .padding(top = 17.dp)
            .height(750.dp)
            .background(Color.White),
        content = {
            items(posts.images.size) { index ->
                Card(
                    colors = CardColors(Color.White, Color.White, Color.White, Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 3.dp)
                        .height(400.dp)
                        .padding(start = 12.dp, end = 12.dp, bottom = 20.dp)
                        .shadow(5.dp, shape = RoundedCornerShape(13.dp), false, Color.Black)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = 12.dp, end = 12.dp, top = 9.dp)
                            .padding(4.dp, 8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(0.35f)
                        ) {
                            Image(
                                painter = painterResource(id = posts.images[index].image),
                                contentDescription = "Image $index",
                                contentScale = ContentScale.FillHeight,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(20.dp)),
                            )
                        }
                        Text(
                            text = "Mô tả:",
                            style = TextStyle(
                                color = Color.Black,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier
                                .padding(top = 5.dp)
                        )
                        val text = truncateString(posts.images[index].description ?: "")
                        Text(
                            text = text,
                            style = TextStyle(
                                color = Color(0xFF6D6D6D),
                            ),
                            modifier = Modifier
                                .padding(top = 5.dp, bottom = 10.dp)
                                .wrapContentHeight()
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(0.7f)
                        ) {
                            //-1 là không có picture trả lời (null)
                            LoadEachImage(
                                image = posts.images[index].answerPicture ?: -1,
                                index = index
                            )
                        }
                        Row(
                            modifier = Modifier
                                .padding(top = 12.dp)
                                .fillMaxWidth()
                                .fillMaxHeight(0.5f)
                        ) {
                            IconButton(
                                onClick = { /*TODO*/ },
                                modifier = Modifier
                                    .padding(start = 6.dp, end = 2.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_gallery),
                                    contentDescription = "",
                                    tint = Color.Gray
                                )
                            }
                            IconButton(
                                onClick = { /*TODO*/ }
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_paperclip),
                                    contentDescription = "Paperclip",
                                    tint = Color.Gray,
                                )
                            }
                        }
                    }
                }
            }
            item {
                GeneralAnswer()
            }
        }
    )
}

@Composable
fun PostAnswerScreen(
    navigateToWorkersQuestionScreen: () -> Unit
) {
    Scaffold(
        topBar = {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(Color(0xFF007E2F))

            )
        },
        bottomBar = {
            Button(
                onClick = navigateToWorkersQuestionScreen,
                colors = ButtonColors(
                    containerColor = Color(0xFF007e2f),
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFF007e2f),
                    disabledContentColor = Color.White,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 5.dp)
            ) {
                Text(text = "GỬI")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            GeneratePostInPostAnswerScreen()
        }
    }
}