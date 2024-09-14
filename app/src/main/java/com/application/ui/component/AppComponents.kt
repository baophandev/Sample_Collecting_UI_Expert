package com.application.ui.component

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.application.R
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@Composable
fun TitleText(
    modifier: Modifier = Modifier,
    text: String,
    textSize: TextUnit,
    color: Color
) {
    Text(
        modifier = modifier.padding(24.dp),
        text = text,
        fontSize = textSize,
        fontWeight = FontWeight.SemiBold,
        textAlign = TextAlign.Center,
        letterSpacing = 2.sp,
        color = color,
        lineHeight = 40.sp
    )
}

@Composable
fun CustomTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
) {
    TextField(
        modifier = modifier.clip(RoundedCornerShape(15.dp)),
        singleLine = singleLine,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        textStyle = textStyle,
        readOnly = readOnly,
        enabled = enabled,
        placeholder = placeholder,
        value = value,
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = colorResource(id = R.color.light_gray),
            focusedContainerColor = colorResource(id = R.color.light_gray),
            unfocusedTextColor = Color.Black,
            focusedTextColor = Color.Black,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent
        ),
        onValueChange = onValueChange
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDatePicker(
    modifier: Modifier = Modifier,
    fieldName: String = "Date",
    initValue: Long? = null,
    pickerTitle: String = "Select date",
    isError: Boolean = false,
    onDateChange: (Long) -> Unit
) {
    val dateFormatter =
        DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneOffset.UTC)
    val defaultDateText = if (initValue != null)
        dateFormatter.format(Instant.ofEpochMilli(initValue))
    else stringResource(id = R.string.default_date)

    val state = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(defaultDateText) }

    Column(modifier = modifier) {
        Text(
            modifier = Modifier.padding(start = 10.dp),
            text = fieldName,
            fontWeight = FontWeight.W400,
            fontSize = 15.sp
        )

        Spacer(modifier = Modifier.size(5.dp))

        Card(
            colors = CardDefaults.cardColors(
                containerColor = colorResource(id = R.color.light_gray)
            ),
            border = if (isError) BorderStroke(
                width = 2.dp,
                color = colorResource(id = R.color.red)
            )
            else BorderStroke(
                width = 0.dp,
                color = Color.Transparent
            )
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 5.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    modifier = Modifier.padding(start = 5.dp),
                    text = selectedDate,
                    fontWeight = FontWeight.W400,
                    fontSize = 13.sp
                )
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(
                        modifier = Modifier.size(30.dp),
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Date picker"
                    )
                }
            }
        }

        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = {
                    selectedDate = defaultDateText
                    showDatePicker = false
                },
                confirmButton = {
                    CustomButton(
                        text = "OK",
                        textSize = 18.sp,
                        background = colorResource(id = R.color.sky_blue)
                    ) {
                        state.selectedDateMillis?.let {
                            selectedDate = dateFormatter.format(Instant.ofEpochMilli(it))
                            onDateChange(it)
                            showDatePicker = false
                        }
                    }
                }
            ) {
                DatePicker(
                    state = state,
                    title = {
                        Text(
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                            text = pickerTitle,
                            fontWeight = FontWeight.W500,
                            fontSize = 24.sp
                        )
                    },
                )
            }
        }
    }
}

@Composable
fun NameAndValueField(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    fieldName: String,
    fieldNameSize: TextUnit = 16.sp,
    fieldNameWeight: FontWeight = FontWeight.Bold,
    fieldNameColor: Color = Color.White,
    fieldValue: String,
    fieldValueSize: TextUnit = 16.sp,
    fieldValueWeight: FontWeight = FontWeight.Normal,
    fieldValueColor: Color = Color.White,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = fieldName,
            fontSize = fieldNameSize,
            fontWeight = fieldNameWeight,
            color = fieldNameColor
        )
        Text(
            text = fieldValue,
            fontSize = fieldValueSize,
            fontWeight = fieldValueWeight,
            color = fieldValueColor
        )
    }
}

@Composable
fun CustomButton(
    modifier: Modifier = Modifier,
    text: String,
    textSize: TextUnit,
    textColor: Color = Color.White,
    icon: Int? = null,
    background: Color = Color.Blue,
    border: BorderStroke? = null,
    action: () -> Unit
) {
    Button(
        modifier = modifier,
        contentPadding = PaddingValues(
            top = 10.dp,
            bottom = 10.dp,
            start = 20.dp,
            end = 20.dp
        ),
        border = border,
        colors = ButtonDefaults.buttonColors(
            containerColor = background
        ),
        onClick = action
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Image(
                    modifier = Modifier.size(30.dp),
                    painter = painterResource(id = icon),
                    contentDescription = "Login icon"
                )

                Spacer(modifier = Modifier.size(10.dp))
            }

            Text(
                text = text,
                fontSize = textSize,
                color = textColor
            )
        }
    }
}

@Composable
fun CustomCircularProgressIndicator(
    modifier: Modifier = Modifier,
    text: String? = null
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.secondary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
        if (text != null) {
            Spacer(modifier = Modifier.size(10.dp))
            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = FontWeight.W500,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Composable
fun LoadingScreen(modifier: Modifier = Modifier, text: String) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(255, 255, 255, 154)),
        contentAlignment = Alignment.Center
    ) {
        CustomCircularProgressIndicator(text = text)
    }
}

@Composable
fun Scrim(onClose: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier
            .pointerInput(onClose) { detectTapGestures { onClose() } }
            .semantics(mergeDescendants = true) {
                contentDescription = "Close"
                onClick {
                    onClose()
                    true
                }
            }
            .onKeyEvent {
                if (it.key == Key.Escape) {
                    onClose()
                    true
                } else {
                    false
                }
            }
            .background(Color.DarkGray.copy(alpha = 0.75f))
    )
}

@Composable
fun CustomSnackBarHost(
    snackBarHostState: SnackbarHostState,
    dismissAction: @Composable (() -> Unit)? = null
) {
    SnackbarHost(hostState = snackBarHostState) {
        Snackbar(
            containerColor = colorResource(id = R.color.red),
            dismissAction = dismissAction
        ) {
            val visuals = snackBarHostState.currentSnackbarData?.visuals
            val message = visuals?.message
            Text(text = message ?: "")
        }
    }
}

@Composable
fun FullScreenImage(
    uri: Uri,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    onTapGesture: ((Offset) -> Unit)? = null
) {
    val context = LocalContext.current

    BoxWithConstraints(
        modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Scrim(onDismiss, Modifier.fillMaxSize())

        var zoomed by remember { mutableStateOf(false) }
        var zoomOffset by remember { mutableStateOf(Offset.Zero) }
        var isTap by remember { mutableStateOf(false) }

        var offset by remember { mutableStateOf(Offset.Zero) }
        var scale by remember { mutableFloatStateOf(1f) }
        val state = rememberTransformableState { zoomChange, offsetChange, _ ->
            isTap = false
            scale = (scale * zoomChange).coerceIn(1f, 5f)

            val extraWidth = (scale - 1) * constraints.maxWidth
            val extraHeight = (scale - 1) * constraints.maxHeight
            val maxX = extraWidth / 2
            val maxY = extraHeight / 2
            offset = Offset(
                x = (offset.x + scale * offsetChange.x).coerceIn(-maxX, maxX),
                y = (offset.y + scale * offsetChange.y).coerceIn(-maxY, maxY)
            )
        }

        AsyncImage(
            model = ImageRequest.Builder(context).data(uri).build(),
            contentScale = ContentScale.FillWidth,
            alignment = Alignment.Center,
            contentDescription = null,
            modifier = modifier
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = onTapGesture,
                        onDoubleTap = { tapOffset ->
                            isTap = true
                            zoomOffset = if (zoomed) Offset.Zero else
                                calculateOffset(tapOffset, size)
                            zoomed = !zoomed
                        }
                    )
                }
                .graphicsLayer {
                    if (isTap) {
                        scaleX = if (zoomed) 2f else 1f
                        scaleY = if (zoomed) 2f else 1f
                        translationX = zoomOffset.x
                        translationY = zoomOffset.y
                    } else {
                        scaleX = scale
                        scaleY = scale
                        translationX = offset.x
                        translationY = offset.y
                    }
                }
                .transformable(state = state)
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(15.dp),
            contentAlignment = Alignment.TopStart
        ) {
            IconButton(modifier = Modifier.size(40.dp), onClick = onDismiss) {
                Icon(
                    modifier = Modifier.fillMaxSize(),
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
        }
    }
}

fun calculateOffset(tapOffset: Offset, size: IntSize): Offset {
    val offsetX = (-(tapOffset.x - (size.width / 2f)) * 2f)
        .coerceIn(-size.width / 2f, size.width / 2f)
    return Offset(offsetX, 0f)
}

@Composable
fun StageContainer(
    title: String,
    description: String? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(30.dp),
                spotColor = Color.Black,
                ambientColor = Color.Black
            )
            .wrapContentSize()
            .fillMaxWidth(),
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(id = R.color.gray_color)
        ),
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(text = title)
            description?.let { Text(text = it) }
        }
    }
}


@Composable
fun FormContainer(
    modifier: Modifier = Modifier,
    name: String,
    onModifyClicked: () -> Unit,
    onDeleteClicked: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(30.dp),
                spotColor = Color.Black,
                ambientColor = Color.Black
            )
            .background(colorResource(id = R.color.gray_color))
            .clip(RoundedCornerShape(10.dp)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = name,
            modifier = Modifier
                .padding(start = 15.dp)
                .wrapContentWidth()
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onModifyClicked) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit field"
                )
            }
            onDeleteClicked?.let {
                Spacer(modifier = Modifier.size(5.dp))

                IconButton(onClick = it) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = colorResource(id = R.color.red)
                    )
                }
            }
        }
    }
}

object RegexValidation {
    val EMAIL: Regex = Regex("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", RegexOption.IGNORE_CASE)
}