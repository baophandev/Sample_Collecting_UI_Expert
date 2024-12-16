package com.application.ui.screen

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.application.R
import com.application.constant.UiStatus
import com.application.data.entity.Post
import com.application.ui.component.CustomCircularProgressIndicator
import com.application.ui.viewmodel.PostDetailViewModel
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import com.application.data.entity.Comment
import com.application.data.entity.GeneralComment
import com.application.ui.component.BotNavigationBar

@Composable
fun PostDetailScreen(
    viewModel: PostDetailViewModel = hiltViewModel(),
    navigateToQuestions: () -> Unit,
    navigateToHome: () -> Unit,
    navigateToConversations: () -> Unit,
) {
    val state by viewModel.state.collectAsState()

    when (state.status) {
        UiStatus.LOADING -> CustomCircularProgressIndicator(
            text = stringResource(id = R.string.loading)
        )

        UiStatus.ERROR -> navigateToQuestions()
        UiStatus.SUCCESS -> {
            val filePagingItems = viewModel.filesFlow.collectAsLazyPagingItems()

            Scaffold(
                topBar = { state.post?.let { post -> HeaderSection(post) } },
                bottomBar = {
                    BotNavigationBar(
                        onQuestionsClick = navigateToQuestions,
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
                LazyColumn(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    items(
                        count = filePagingItems.itemCount,
                        key = filePagingItems.itemKey { it.id }
                    ) { index ->
                        val item = filePagingItems[index] ?: return@items

                        ExpertResponseCard(
                            image = item.image,
                            description = item.description,
                            comment = item.comment,
                            onAttachmentClick = viewModel::startDownload,
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                    }

                    item {
                        ConclusionSection(
                            generalComment = state.post?.generalComment,
                            onAttachmentClick = viewModel::startDownload
                        )
                    }
                }
            }
        }

        else -> {}
    }
}


@Composable
private fun HeaderSection(post: Post) {
    var isShowDetail by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF4CAF50))
            .padding(8.dp)
    ) {
        Text(
            text = "${post.owner.firstName} ${post.owner.lastName}",
            style = MaterialTheme.typography.titleLarge.copy(
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        )
        if (isShowDetail) {
            Text(
                text = post.title,
                style = MaterialTheme.typography.titleSmall.copy(color = Color.White)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            if (isShowDetail) {
                IconButton(
                    onClick = { isShowDetail = !isShowDetail }, modifier = Modifier
                        .height(10.dp)
                        .fillMaxWidth()
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_close),
                        contentDescription = "Collapse",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            } else {
                IconButton(
                    modifier = Modifier
                        .height(10.dp)
                        .fillMaxWidth(),
                    onClick = { isShowDetail = !isShowDetail },
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_show),
                        contentDescription = "Expand",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

/**
 * @param onAttachmentClick (Uri, fileName) -> Unit
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ConclusionSection(
    generalComment: GeneralComment? = null,
    onAttachmentClick: (Uri, String) -> Unit,
) {
    Column(modifier = Modifier.padding(10.dp)) {
        Text(
            text = stringResource(id = R.string.expert_conclusion),
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color(0, 126, 47),
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(modifier = Modifier.height(2.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(5.dp, shape = RoundedCornerShape(8.dp))
                .padding(horizontal = 5.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                TextField(
                    value = generalComment?.content ?: "",
                    placeholder = {
                        Text(text = stringResource(R.string.no_comment))
                    },
                    onValueChange = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, shape = RoundedCornerShape(8.dp)),

                    )
                Spacer(modifier = Modifier.height(5.dp))

                generalComment?.attachments?.let { attachments ->
                    FlowRow(
                        maxItemsInEachRow = 1,
                        maxLines = 5
                    ) {
                        attachments.forEach { attachment ->
                            FileItem(
                                fileName = attachment.name,
                                iconRes = R.drawable.ic_document,
                                onClick = {
                                    onAttachmentClick(Uri.parse(attachment.url), attachment.name)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * @param onAttachmentClick (Uri, fileName) -> Unit
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ExpertResponseCard(
    image: Uri,
    description: String? = null,
    comment: Comment? = null,
    onAttachmentClick: (Uri, String) -> Unit,
) {
    val context = LocalContext.current

    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 5.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(image)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(150.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = stringResource(id = R.string.description) + ":",
                    color = Color.Black
                )
                Spacer(modifier = Modifier.size(2.dp))
                Text(
                    text = description ?: stringResource(R.string.no_description),
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Column {
                Text(
                    text = stringResource(id = R.string.expert_answer),
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.Black
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF0F0F0), shape = RoundedCornerShape(8.dp))
                        .padding(16.dp)
                ) {
                    Text(
                        text = comment?.content ?: stringResource(R.string.no_answer),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))

            comment?.attachments?.let { attachments ->
                FlowRow(
                    maxItemsInEachRow = 1,
                    maxLines = 5
                ) {
                    attachments.forEach { attachment ->
                        FileItem(
                            fileName = attachment.name,
                            iconRes = R.drawable.ic_document,
                            onClick = {
                                onAttachmentClick(Uri.parse(attachment.url), attachment.name)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FileItem(fileName: String, iconRes: Int, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .clickable(onClick = onClick)
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            tint = Color.Unspecified,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = fileName,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color(0, 126, 47),
                textDecoration = TextDecoration.Underline
            ),
        )
    }
}
