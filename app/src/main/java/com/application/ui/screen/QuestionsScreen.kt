package com.application.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.application.R
import com.application.data.entity.Post
import com.application.ui.component.BotNavigationBar
import com.application.ui.component.PagingLayout
import com.application.ui.component.TitleText
import com.application.ui.viewmodel.QuestionsViewModel

@Composable
fun QuestionsScreen(
    viewModel: QuestionsViewModel = hiltViewModel(),
    navigateToHome: () -> Unit,
    navigateToConversations: () -> Unit,
    navigateToPostDetail: (String) -> Unit
) {
    val postPagingItems = viewModel.postFlow.collectAsLazyPagingItems()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(null) { viewModel.initCheckAnsweredDebounce(postPagingItems::refresh) }

    Scaffold(
        topBar = { TopBar(viewModel::searchPost) },
        bottomBar = {
            BotNavigationBar(
                onQuestionsClick = {},
                onExpertChatClick = navigateToConversations
            ) {
                IconButton(
                    modifier = Modifier.size(50.dp),
                    onClick = navigateToHome
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
        Column(modifier = Modifier.padding(innerPadding)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = state.isAnswered,
                    onCheckedChange = viewModel::updateIsAnswered
                )
                Text(
                    modifier = Modifier.clickable { viewModel.updateIsAnswered(!state.isAnswered) },
                    text = stringResource(R.string.answered_post)
                )
            }
            PagingLayout(
                pagingItems = postPagingItems,
                itemKey = postPagingItems.itemKey { it.id },
                itemsContent = { post ->
                    QuestionTemplate(
                        post = post,
                        onClick = { navigateToPostDetail(post.id) }
                    )
                    Spacer(modifier = Modifier.size(15.dp))
                },
                noItemContent = {
                    Icon(
                        painter = painterResource(id = R.drawable.empty_icon),
                        contentDescription = "No post",
                        modifier = Modifier.size(100.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    TitleText(
                        text = stringResource(id = R.string.no_post),
                        textSize = 15.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            )
        }
    }

}

@Composable
private fun TopBar(
    onSearchChange: (String) -> Unit,
) {
    var value by remember { mutableStateOf("") }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(65.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        BasicTextField(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(30.dp)
                )
                .fillMaxWidth(.8f)
                .fillMaxHeight(.5f),
            value = value,
            onValueChange = { newText ->
                value = newText
                onSearchChange(newText)
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
                    contentDescription = "Notify",
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                    modifier = Modifier.fillMaxSize(.5f),
                )
            }
        }
    }
}

@Composable
fun QuestionTemplate(
    post: Post,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val fullName = "${post.owner.firstName} ${post.owner.lastName}"

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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(187.dp)
            ) {
//                ImageGallery(images = images, maxWidth = 352, maxHeight = 187)
                if (post.thumbnail != null) AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(post.thumbnail)
                        .build(),
                    modifier = Modifier.fillMaxSize(),
                    contentDescription = "Thumbnail",
                    contentScale = ContentScale.Crop
                )
                else Image(
                    painter = painterResource(id = R.drawable.ic_launcher_background),
                    modifier = Modifier.fillMaxSize(),
                    contentDescription = "Default Thumbnail",
                    contentScale = ContentScale.FillBounds
                )
            }

            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(22.dp)
                ) {
                    Text(
                        text = fullName,
                        modifier = Modifier
                            .padding(start = 12.dp, end = 12.dp, top = 5.dp),
                        style = TextStyle(
                            fontSize = 14.sp,
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
                            text = post.title,
                            modifier = Modifier
                                .padding(start = 12.dp, end = 12.dp, top = 5.dp),
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF625B71)
                            ),
                            maxLines = 2
                        )
                    }
                }
            }
        }
    }
}

