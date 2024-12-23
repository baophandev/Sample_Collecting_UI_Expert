package com.application.ui.screen

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color.rgb
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.application.ui.component.PagingLayout
import com.application.ui.viewmodel.ChatViewModel
import com.sc.library.chat.data.entity.ReceivingMessage

@Composable
fun ChatScreen(
    viewModel: ChatViewModel = hiltViewModel(),
    navigateToConversations: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val messagesFlow = viewModel.messagesFlow.collectAsLazyPagingItems()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopChatBar(
                title = state.conversation?.title ?: "",
                modifier = Modifier.padding(bottom = 3.dp),
                route = navigateToConversations
            )
        }, bottomBar = {
            ReplyBar(
                modifier = Modifier.fillMaxWidth(),
                message = state.text,
                onMessageChange = viewModel::updateMessage,
                onSendClick = viewModel::sendMessage,
                onSendImage = { imageBitmap ->

                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            CaptionBar(text = stringResource(id = R.string.caption_question))
            ChatList(pagingItems = messagesFlow)
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopChatBar(
    modifier: Modifier = Modifier,
    title: String,
    route: () -> Unit
) {
    TopAppBar(modifier = modifier, colors = TopAppBarDefaults.topAppBarColors().copy(
        containerColor = colorResource(id = R.color.main_green)
    ), title = {
        Text(
            text = title,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
    }, navigationIcon = {
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
    }, scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    )
}


@Composable
fun ReplyBar(
    modifier: Modifier = Modifier,
    message: String = "",
    onMessageChange: (String) -> Unit,
    onSendImage: (Bitmap) -> Unit,
    onSendClick: () -> Unit
) {
    val context = LocalContext.current

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { it?.let(onSendImage) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
        else Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
    }

    Row(
        modifier = modifier
            .background(Color.White)
            .size(50.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            modifier = Modifier.size(35.dp),
            onClick = {
                val permissionCheckResult =
                    ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                    cameraLauncher.launch()
                } else {
                    // Request a permission
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                }
            }) {
            Icon(
                modifier = Modifier.fillMaxSize(.85f),
                painter = painterResource(id = R.drawable.camera_icon),
                contentDescription = null,
                tint = Color.Unspecified
            )
        }
        Spacer(modifier = Modifier.size(5.dp))
        IconButton(modifier = Modifier.size(35.dp), onClick = { /*TODO*/ }) {
            Icon(
                modifier = Modifier.fillMaxSize(.85f),
                painter = painterResource(id = R.drawable.image_icon),
                contentDescription = null,
                tint = Color.Unspecified
            )
        }
        Spacer(modifier = Modifier.size(5.dp))
        IconButton(modifier = Modifier.size(35.dp), onClick = { /*TODO*/ }) {
            Icon(
                modifier = Modifier.fillMaxSize(.8f),
                painter = painterResource(id = R.drawable.mic_icon),
                contentDescription = null,
                tint = Color.Unspecified
            )
        }
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
            onClick = {
                if (message.isNotBlank()) onSendClick()
            }
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
fun CaptionBar(
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
        })
}

@Composable
fun ChatList(
    modifier: Modifier = Modifier,
    pagingItems: LazyPagingItems<ReceivingMessage>,
) {
    val context = LocalContext.current

    PagingLayout(
        modifier = modifier,
        pagingItems = pagingItems,
        itemsContent = { message ->
            val isOwnMessage = true //username
            Box(
                contentAlignment = if (isOwnMessage) {
                    Alignment.CenterEnd
                } else Alignment.CenterStart, modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                    ) {
                        items(message.attachments) { image ->
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(image.url)
                                    .fallback(drawableResId = R.drawable.ic_launcher_background)
                                    .error(drawableResId = R.drawable.ic_launcher_background)
                                    .build(),
                                contentDescription = null,
                                modifier = Modifier
                                    .sizeIn(
                                        maxWidth = 160.dp,
                                        minWidth = 16.dp,
                                        maxHeight = 90.dp,
                                        minHeight = 9.dp,
                                    )
                                    .background(Color.Gray.copy(alpha = .4f)),
                                contentScale = ContentScale.Fit,
                                alignment = Alignment.TopEnd
                            )
                        }
                    }

                    Text(
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(5.dp, 0.dp),
                        text = "00:00",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
                Column(
                    modifier = Modifier
                        .drawBehind {
                            val trianglePath = Path().apply {
                                if (isOwnMessage) {
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
                                path = trianglePath, color = if (isOwnMessage) Color(
                                    rgb(
                                        229, 239, 255
                                    )
                                ) else Color.White
                            )
                        }
                        .background(
                            color = if (isOwnMessage) Color(
                                rgb(
                                    229, 239, 255
                                )
                            ) else Color.White, shape = RoundedCornerShape(15.dp)
                        )
                        .padding(8.dp)
                ) {
                    Text(
                        text = message.text,
                        color = Color.Black
                    )

                    Text(
                        modifier = Modifier.align(Alignment.End),
                        text = "00:00",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
            }
            Spacer(modifier = Modifier.size(10.dp))
        },
        itemKey = pagingItems.itemKey { it.id },
        noItemContent = {

        }
    )
}

