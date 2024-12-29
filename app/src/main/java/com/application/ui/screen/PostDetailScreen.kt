package com.application.ui.screen

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.application.R
import com.application.constant.UiStatus
import com.application.data.entity.Comment
import com.application.data.entity.GeneralComment
import com.application.data.entity.Post
import com.application.ui.component.BotNavigationBar
import com.application.ui.component.CustomButton
import com.application.ui.component.CustomSnackBarHost
import com.application.ui.viewmodel.PostDetailViewModel
import io.github.nhatbangle.sc.attachment.entity.Attachment

@Composable
fun PostDetailScreen(
    viewModel: PostDetailViewModel = hiltViewModel(),
    navigateToQuestions: () -> Unit,
    navigateToHome: () -> Unit,
    navigateToConversations: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }

    if (state.error != null) {
        val error = stringResource(id = state.error!!)
        LaunchedEffect(key1 = state.error) {
            val result = snackBarHostState.showSnackbar(
                message = error,
                withDismissAction = true,
                duration = SnackbarDuration.Short
            )
            if (result == SnackbarResult.Dismissed) viewModel.gotError()
        }
    }

    Scaffold(
        topBar = { state.post?.let { post -> HeaderSection(post) } },
        bottomBar = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                CustomButton(
                    modifier = Modifier
                        .fillMaxWidth(.9f)
                        .clip(RoundedCornerShape(2.dp)),
                    text = if (state.post?.isResolved == false) stringResource(R.string.submit)
                    else stringResource(R.string.back),
                    textSize = 14.sp,
                    background = MaterialTheme.colorScheme.primary,
                    action = {
                        if (state.post?.isResolved == true) navigateToQuestions()
                        else viewModel.submit()
                    }
                )

                BotNavigationBar(
                    onQuestionsClick = navigateToQuestions,
                    onExpertChatClick = navigateToConversations
                ) {
                    IconButton(
                        modifier = Modifier.size(38.dp),
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
        },
        snackbarHost = {
            CustomSnackBarHost(
                snackBarHostState = snackBarHostState,
                dismissAction = {
                    IconButton(
                        modifier = Modifier
                            .padding(end = 10.dp)
                            .size(30.dp),
                        onClick = viewModel::gotError
                    ) {
                        Icon(
                            modifier = Modifier.fillMaxSize(),
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Close"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        when (state.status) {
            UiStatus.LOADING -> LoadingScreen(text = stringResource(R.string.loading))
            UiStatus.ERROR -> navigateToQuestions()
            UiStatus.SUCCESS -> LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                itemsIndexed(
                    items = state.files,
                    key = { _, file -> file.id },
                ) { index, file ->
                    FileInPostTemplate(
                        readOnly = state.post?.isResolved == true,
                        image = file.image,
                        description = file.description,
                        comment = file.comment,
                        onCommentChange = { viewModel.updateComment(index, file.id, it) },
                        onAddAttachment = { viewModel.updateComment(index, file.id, it) },
                        onAttachmentClick = viewModel::startDownload,
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                }

                item {
                    ConclusionSection(
                        readOnly = state.post?.isResolved == true,
                        generalComment = state.post?.generalComment,
                        onCommentChange = viewModel::updateGeneralComment,
                        onAddAttachment = viewModel::updateGeneralComment,
                        onAttachmentClick = viewModel::startDownload
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                }
            }

            else -> {}
        }
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
    readOnly: Boolean = false,
    generalComment: GeneralComment? = null,
    onCommentChange: (String) -> Unit,
    onAddAttachment: (List<Attachment>) -> Unit,
    onAttachmentClick: (Attachment) -> Unit,
) {
    val context = LocalContext.current
    val pickFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents(),
        onResult = {
            val attachments = it.map { uri -> extractFileName(context.contentResolver, uri) }
            onAddAttachment(attachments)
        }
    )

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
                    readOnly = readOnly,
                    modifier = Modifier.fillMaxWidth(),
                    value = generalComment?.content ?: "",
                    onValueChange = onCommentChange,
                    placeholder = {
                        Text(
                            text = stringResource(R.string.no_answer),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black
                        )
                    },
                    colors = TextFieldDefaults.colors().copy(
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent
                    )
                )
                Spacer(modifier = Modifier.height(5.dp))

                FlowRow(
                    maxItemsInEachRow = 1,
                    maxLines = 5
                ) {
                    generalComment?.attachments?.let {
                        it.forEach { attachment ->
                            FileItem(
                                fileName = attachment.name,
                                iconRes = R.drawable.ic_document,
                                onClick = { onAttachmentClick(attachment) }
                            )
                        }
                    }
                }

                if (!readOnly) {
                    Row {
                        IconButton(onClick = { pickFileLauncher.launch(input = "*/*") }) {
                            Icon(
                                modifier = Modifier.size(15.dp),
                                painter = painterResource(R.drawable.ic_paperclip),
                                tint = Color.Black,
                                contentDescription = "Pick up attachments"
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FileInPostTemplate(
    readOnly: Boolean = false,
    image: Uri,
    description: String? = null,
    comment: Comment? = null,
    onCommentChange: (String) -> Unit,
    onAddAttachment: (List<Attachment>) -> Unit,
    onAttachmentClick: (Attachment) -> Unit,
) {
    val context = LocalContext.current
    val pickFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents(),
        onResult = {
            val attachments = it.map { uri -> extractFileName(context.contentResolver, uri) }
            onAddAttachment(attachments)
        }
    )

    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
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
                TextField(
                    readOnly = readOnly,
                    modifier = Modifier.fillMaxWidth(),
                    value = comment?.content ?: "",
                    onValueChange = onCommentChange,
                    placeholder = {
                        Text(
                            text = stringResource(R.string.no_answer),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black
                        )
                    },
                    colors = TextFieldDefaults.colors().copy(
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent
                    )
                )
            }
            Spacer(modifier = Modifier.height(10.dp))

            FlowRow(
                maxItemsInEachRow = 1,
                maxLines = 5
            ) {
                comment?.attachments?.let { attachments ->
                    attachments.forEach { attachment ->
                        FileItem(
                            fileName = attachment.name,
                            iconRes = R.drawable.ic_document,
                            onClick = { onAttachmentClick(attachment) }
                        )
                    }
                }
            }

            if (!readOnly) {
                Row {
                    IconButton(onClick = { pickFileLauncher.launch(input = "*/*") }) {
                        Icon(
                            modifier = Modifier.size(15.dp),
                            painter = painterResource(R.drawable.ic_paperclip),
                            tint = Color.Black,
                            contentDescription = "Pick up attachments"
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

private fun extractFileName(resolver: ContentResolver, uri: Uri): Attachment {
    var fileName = "unknown file"
    val mimeType = resolver.getType(uri) ?: ""
    resolver
        .query(uri, null, null, null, null)
        ?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            fileName = cursor.getString(nameIndex)
        }
    return Attachment(
        id = "$fileName-$mimeType-${System.currentTimeMillis()}",
        name = fileName,
        type = mimeType,
        url = uri.toString()
    )
}