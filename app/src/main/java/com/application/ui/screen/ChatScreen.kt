package com.application.ui.screen

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color.rgb
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
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
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.application.R
import com.application.ui.viewmodel.ChatViewModel
import com.application.util.Validation
import io.github.nhatbangle.sc.attachment.entity.Attachment
import io.github.nhatbangle.sc.chat.constant.MessageType
import io.github.nhatbangle.sc.chat.data.entity.ReceivingMessage
import java.sql.Timestamp
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@Composable
fun ChatScreen(
    viewModel: ChatViewModel = hiltViewModel(),
    navigateToConversations: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val messages by viewModel.messages.collectAsState()
    val messagePagingItems = viewModel.messagePagingFlow.collectAsLazyPagingItems()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopBar(
                title = state.conversation?.title ?: "",
                route = navigateToConversations
            )
//            Column {
//                TopBar(
//                    modifier = Modifier.padding(bottom = 3.dp),
//                    title = state.conversation?.title ?: "",
//                    route = navigateToConversations
//                )
//                CaptionBar(
//                    modifier = Modifier.fillMaxWidth(),
//                    text = stringResource(id = R.string.caption_question)
//                )
//            }
        },
        bottomBar = {
            ReplyBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp, end = 4.dp)
                    .background(Color.White)
                    .height(50.dp),
                message = state.text,
                onMessageChange = {
                    if (Validation.checkLongText(it))
                        viewModel.updateMessage(it)
                },
                onSendClick = viewModel::sendMessage,
                onCameraResult = { viewModel.sendMessage(listOf(it)) },
                onGalleryResult = viewModel::sendMessage,
//                onRecordResult = {
//                }
            )
        }
    ) { innerPadding ->
        MessageList(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            pagingItems = messagePagingItems,
            messages = messages,
            isSendingMessage = { viewModel.isSendingMessage(it.sender.id) }
        )
    }
}

@Composable
private fun MessageList(
    modifier: Modifier = Modifier,
    pagingItems: LazyPagingItems<ReceivingMessage>,
    messages: List<ReceivingMessage>,
    isSendingMessage: (ReceivingMessage) -> Boolean,
) {
    val state = rememberLazyListState()

    LaunchedEffect(null) {
        state.scrollToItem(pagingItems.itemCount)
    }

    LazyColumn(
        state = state,
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        reverseLayout = true
    ) {
        if (pagingItems.itemCount == 0 && messages.isEmpty())
            item {
                Text(
                    text = stringResource(R.string.no_messages),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.LightGray.copy(alpha = .9f)
                )
            }
        else {
            items(items = messages.reversed(), key = { it.id }) { message ->
                MessageTemplate(
                    message = message,
                    isSendingMessage = isSendingMessage(message)
                )
                Spacer(modifier = Modifier.size(10.dp))
            }
            items(count = pagingItems.itemCount, key = pagingItems.itemKey { it.id }) {
                val message = pagingItems[it] ?: return@items
                MessageTemplate(
                    message = message,
                    isSendingMessage = isSendingMessage(message)
                )
                Spacer(modifier = Modifier.size(10.dp))
            }
        }
    }
}

@Composable
private fun MessageTemplate(
    modifier: Modifier = Modifier,
    message: ReceivingMessage,
    isSendingMessage: Boolean = true
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = if (isSendingMessage) Alignment.End
        else Alignment.Start,
    ) {
        when (message.type) {
            MessageType.TEXT -> MessageText(message = message, isSendingMessage = isSendingMessage)
            MessageType.FILE -> {
                message.attachments?.forEach {
                    MessageFile(
                        modifier = Modifier
                            .sizeIn(
                                maxWidth = 300.dp,
                                maxHeight = 400.dp
                            )
                            .wrapContentSize(),
                        attachment = it,
                        createdAt = message.createdAt
                    )
                }
            }

            MessageType.BOTH -> {
                message.attachments?.forEach {
                    MessageFile(
                        modifier = Modifier
                            .sizeIn(
                                maxWidth = 300.dp,
                                maxHeight = 400.dp
                            )
                            .wrapContentSize(),
                        attachment = it,
                        createdAt = null
                    )
                }
                MessageText(message = message, isSendingMessage = isSendingMessage)
            }
        }
    }
}

@Composable
private fun MessageText(
    modifier: Modifier = Modifier,
    isSendingMessage: Boolean,
    message: ReceivingMessage
) {
    val time = DateTimeFormatter
        .ofPattern("dd/MM/yy h:mma")
        .withZone(ZoneOffset.systemDefault())
        .format(message.createdAt.toInstant())
    val messageText = message.text ?: return

    Column(
        modifier = modifier
            .drawBehind {
                val trianglePath = Path().apply {
                    if (isSendingMessage) {
                        moveTo(size.width, size.height)
                        lineTo(size.width, size.height)
                        lineTo(
                            size.width, size.height
                        )
                        close()
                    } else {
                        moveTo(0f, size.height)
                        lineTo(0f, size.height)
                        lineTo(0.dp.toPx(), size.height)
                        close()
                    }
                }
                drawPath(
                    path = trianglePath, color = if (isSendingMessage) Color(
                        rgb(
                            229, 239, 255
                        )
                    ) else Color.White
                )
            }
            .background(
                color = if (isSendingMessage) Color(rgb(229, 239, 255))
                else Color.White, shape = RoundedCornerShape(15.dp)
            )
            .padding(8.dp)
    ) {
        Text(
            text = messageText,
            color = Color.Black
        )

        Text(
            modifier = Modifier.align(Alignment.End),
            text = time,
            color = Color.Gray,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun MessageFile(
    modifier: Modifier = Modifier,
    attachment: Attachment,
    createdAt: Timestamp?
) {
    val context = LocalContext.current
    val time = createdAt?.let {
        DateTimeFormatter
            .ofPattern("dd/MM/yy h:mma")
            .withZone(ZoneOffset.systemDefault())
            .format(createdAt.toInstant())
    }

    Column(modifier = modifier.background(Color.Transparent)) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(attachment.url)
                .placeholder(drawableResId = R.drawable.ic_launcher_background)
                .fallback(drawableResId = R.drawable.ic_launcher_background)
                .error(drawableResId = R.drawable.ic_launcher_background)
                .build(),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize(.7f)
                .background(Color.Gray.copy(alpha = .4f))
                .clip(RoundedCornerShape(5.dp)),
            contentScale = ContentScale.FillBounds,
            alignment = Alignment.Center
        )

        time?.let {
            Text(
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(5.dp, 0.dp),
                text = time,
                color = Color.Gray,
                fontSize = 12.sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    modifier: Modifier = Modifier,
    title: String,
    route: () -> Unit
) {
    TopAppBar(
        modifier = modifier,
        colors = TopAppBarDefaults.topAppBarColors().copy(
            containerColor = colorResource(id = R.color.main_green)
        ),
        title = {
            Text(
                text = title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(
                onClick = route
            ) {
                Icon(
                    modifier = Modifier.size(55.dp),
                    painter = painterResource(id = R.drawable.leading_icon),
                    contentDescription = null,
                    tint = Color.White,
                )
            }
        },
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    )
}

@Composable
private fun ReplyBar(
    modifier: Modifier = Modifier,
    message: String = "",
    onMessageChange: (String) -> Unit,
    onCameraResult: (Uri) -> Unit,
    onGalleryResult: (List<Uri>) -> Unit,
//    onRecordResult: (Bitmap) -> Unit,
    onSendClick: () -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        CameraButton(
            modifier = Modifier.size(35.dp),
            onCameraResult = onCameraResult
        )
        Spacer(modifier = Modifier.size(5.dp))
        GalleryButton(
            modifier = Modifier.size(35.dp),
            onGalleryResult = onGalleryResult
        )
        Spacer(modifier = Modifier.size(5.dp))
//        RecordButton(
//            modifier = Modifier.size(35.dp),
//            onRecordResult = onRecordResult
//        )
        Spacer(modifier = Modifier.size(10.dp))
        BasicTextField(
            modifier = Modifier
                .background(
                    color = Color(217, 221, 224, 255),
                    shape = RoundedCornerShape(30.dp)
                )
                .fillMaxWidth(.8f)
                .fillMaxHeight(.7f),
            value = message,
            onValueChange = onMessageChange,
            textStyle = TextStyle(
                textAlign = TextAlign.Start
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
                        if (message.isEmpty()) {
                            Text(
                                text = stringResource(id = R.string.message_placeholder),
                                style = LocalTextStyle.current.copy(
                                    color = Color(139, 139, 139, 255),
                                    fontSize = 14.sp
                                )
                            )
                        }
                        innerTextField()
                    }
                }
            },
        )
        Spacer(modifier = Modifier.size(5.dp))
        IconButton(
            modifier = Modifier.size(35.dp),
            onClick = { if (message.isNotBlank()) onSendClick() }
        ) {
            Icon(
                modifier = Modifier.fillMaxSize(.85f),
                painter = painterResource(id = R.drawable.send_icon),
                contentDescription = null,
                tint = Color.Unspecified
            )
        }
    }
}

@Composable
private fun CameraButton(
    modifier: Modifier = Modifier,
    onCameraResult: (Uri) -> Unit
) {
    val context = LocalContext.current

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { it?.let { bitmap -> saveBitmapImage(context, bitmap)?.let(onCameraResult) } }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
    }

    IconButton(
        modifier = modifier,
        onClick = {
            val cameraPermissionCheckResult =
                ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
            if (cameraPermissionCheckResult == PackageManager.PERMISSION_GRANTED)
                cameraLauncher.launch()
            else permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    ) {
        Icon(
            modifier = Modifier.fillMaxSize(.85f),
            painter = painterResource(id = R.drawable.camera_icon),
            contentDescription = null,
            tint = Color.Unspecified
        )
    }
}

private fun saveBitmapImage(context: Context, bitmap: Bitmap): Uri? {
    val timestamp = System.currentTimeMillis()
    val tag = "ChatScreen.saveBitmapImage"
    val resolver = context.contentResolver

    //Tell the media scanner about the new file so that it is immediately available to the user.
    val values = ContentValues().apply {
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        put(MediaStore.Images.Media.DATE_ADDED, timestamp)
        put(MediaStore.Images.Media.DATE_TAKEN, timestamp)
        put(MediaStore.Images.Media.IS_PENDING, true)
    }

    val cleanup: (Uri, Throwable) -> Unit = { uri, exception ->
        uri.let { resolver.delete(uri, null, null) }
        Log.e(tag, exception.localizedMessage, exception)
        Toast.makeText(context, "Cannot save image.", Toast.LENGTH_LONG).show()
    }

    return resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)?.let { uri ->
        try {
            resolver.openOutputStream(uri)?.use { outputStream ->
                try {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                    outputStream.close()
                } catch (e: Exception) {
                    cleanup(uri, e)
                }
                values.put(MediaStore.Images.Media.IS_PENDING, false)
                resolver.update(uri, values, null, null)
            }
        } catch (e: Exception) {
            cleanup(uri, e)
        }
        uri
    }
}

@Composable
private fun GalleryButton(
    modifier: Modifier = Modifier,
    onGalleryResult: (List<Uri>) -> Unit
) {
    val contentsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents(),
        onResult = onGalleryResult
    )

    IconButton(
        modifier = modifier,
        onClick = { contentsLauncher.launch("image/*") }
    ) {
        Icon(
            modifier = Modifier.fillMaxSize(.85f),
            painter = painterResource(id = R.drawable.image_icon),
            contentDescription = null,
            tint = Color.Unspecified
        )
    }
}

//@Composable
//private fun RecordButton(
//    modifier: Modifier = Modifier,
//    onRecordResult: (Bitmap) -> Unit
//) {
//    IconButton(
//        modifier = modifier,
//        onClick = {
//
//        }
//    ) {
//        Icon(
//            modifier = Modifier.fillMaxSize(.85f),
//            painter = painterResource(id = R.drawable.mic_icon),
//            contentDescription = null,
//            tint = Color.Unspecified
//        )
//    }
//}

@Composable
private fun CaptionBar(
    modifier: Modifier = Modifier, text: String
) {
    Column(modifier = modifier
        .background(Color(50, 219, 137, 255))
        .fillMaxWidth()
        .padding(5.dp, 5.dp),
        content = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = text,
                color = Color.White,
                textAlign = TextAlign.Justify
            )
            Text(
                modifier = Modifier
                    .align(Alignment.End)
                    .clickable { },
                text = stringResource(id = R.string.see_details),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline,
            )
        }
    )
}