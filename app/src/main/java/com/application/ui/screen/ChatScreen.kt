package com.application.ui.screen

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color.rgb
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
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
import com.application.R
import com.application.data.entity.ChatData
import kotlinx.coroutines.launch

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
    modifier: Modifier = Modifier, onSendImage: (Bitmap) -> Unit, onSendText: (String) -> Unit
) {
    var value by remember {
        mutableStateOf("")
    }

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
            value = value,
            onValueChange = { newText ->
                value = newText
            },
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
                        if (value.isEmpty()) {
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
        IconButton(modifier = Modifier.size(35.dp), onClick = {
            Log.i("ReplyBar_ChatScreen", "Sending event")
            if (value.isNotBlank()) {
                onSendText(value)
                value = ""
            }
        }) {
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
    state: LazyListState,
    messages: List<ChatData>
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(), state = state
        ) {
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
            items(messages){ message ->
                val isOwnMessage = true //username
                Box(
                    contentAlignment = if (isOwnMessage) {
                        Alignment.CenterEnd
                    } else Alignment.CenterStart, modifier = Modifier.fillMaxWidth()
                ) {
                    message.text?.let {
                        Column(modifier = Modifier
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
                            .padding(8.dp)) {
                            Text(
                                text = it, color = Color.Black
                            )

                            Text(
                                modifier = Modifier.align(Alignment.End),
                                text = "00:00",
                                color = Color.Gray,
                                fontSize = 12.sp
                            )
                        }
                    }
                    message.imageBitmap?.let {
                        Column(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Image(
                                bitmap = it.asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier
                                    .padding(end = 5.dp)
                                    .fillMaxWidth()
                                    .aspectRatio(1f),
                                contentScale = ContentScale.Fit,
                                alignment = Alignment.TopEnd
                            )

                            Text(
                                modifier = Modifier
                                    .align(Alignment.End)
                                    .padding(5.dp, 0.dp),
                                text = "00:00",
                                color = Color.Gray,
                                fontSize = 12.sp
                            )
                        }
                    }

                }
                Spacer(modifier = Modifier.size(10.dp))
            }
        }
    }
}


@Composable
fun ChatScreen(
    modifier: Modifier = Modifier,
    navigateToConversations: () -> Unit
) {
    val messages: SnapshotStateList<ChatData> = remember { mutableStateListOf() }
    val chatState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopChatBar(
                title = "Chuyên gia MiMi",
                modifier = Modifier.padding(bottom = 3.dp),
                route = navigateToConversations
            )
        }, bottomBar = {
            ReplyBar(
                modifier = Modifier.fillMaxWidth(),
                onSendText = { newMessage ->
                    messages.add(ChatData(text = newMessage))
                    coroutineScope.launch {
                        chatState.scrollToItem(messages.size)
                    }
                },
                onSendImage = { imageBitmap ->
                    messages.add(ChatData(imageBitmap = imageBitmap))
                    coroutineScope.launch {
                        chatState.scrollToItem(messages.size)
                    }
                })
        }) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            CaptionBar(text = stringResource(id = R.string.caption_question))
            ChatList(state = chatState, messages = messages)
        }
    }

}

