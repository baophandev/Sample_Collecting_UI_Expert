package com.application.ui.component

import android.net.Uri
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest

private fun calculateOffset(tapOffset: Offset, size: IntSize): Offset {
    val offsetX = (-(tapOffset.x - (size.width / 2f)) * 2f)
        .coerceIn(-size.width / 2f, size.width / 2f)
    return Offset(offsetX, 0f)
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