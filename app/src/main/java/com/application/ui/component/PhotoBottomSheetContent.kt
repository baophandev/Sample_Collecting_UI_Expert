package com.application.ui.component

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.application.R

/**
 * @param onPhotoPress this callback passes a element index which are pressed
 * @param onDeleteImages this callback passes a removing element index of uriList
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PhotoBottomSheetContent(
    uriList: List<Uri>,
    modifier: Modifier = Modifier,
    emptyTextAlignment: Alignment = Alignment.Center,
    onPhotoPress: (Int) -> Unit,
    onDeleteImages: ((List<Uri>) -> Unit)? = null,
    onSelectImages: ((Boolean) -> Unit)? = null
) {
    val context = LocalContext.current
    val haptics = LocalHapticFeedback.current

    var showSelectionMenu by remember { mutableStateOf(false) }
    val selectedImages = remember { mutableStateListOf<Uri>() }

    if (uriList.isEmpty()) {
        Box(
            modifier = modifier,
            contentAlignment = emptyTextAlignment
        ) {
            Text(
                modifier = Modifier.padding(16.dp),
                text = stringResource(id = R.string.no_photos)
            )
        }
    } else {
        Box {
            LazyVerticalStaggeredGrid(
                modifier = modifier,
                columns = StaggeredGridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalItemSpacing = 16.dp,
                contentPadding = PaddingValues(16.dp)
            ) {
                itemsIndexed(uriList) { idx, uri ->
                    Box {
                        AsyncImage(
                            model = ImageRequest.Builder(context).data(uri).build(),
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .combinedClickable(
                                    onClick = {
                                        if (!showSelectionMenu || onDeleteImages == null) {
                                            onPhotoPress(idx)
                                        } else {
                                            if (selectedImages.contains(uri))
                                                selectedImages.remove(uri)
                                            else selectedImages.add(uri)
                                        }
                                    },
                                    onLongClick = {
                                        onSelectImages?.let {
                                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                            showSelectionMenu = true
                                            onSelectImages(true)
                                            selectedImages.add(uri)
                                        }
                                    }
                                ),
                            contentDescription = null
                        )

                        if (selectedImages.contains(uri)) {
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .padding(10.dp)
                                    .background(
                                        color = colorResource(id = R.color.sky_blue),
                                        shape = RoundedCornerShape(50)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    modifier = Modifier.fillMaxSize(.8f),
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Check",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            }

            onDeleteImages?.let { callback ->
                AnimatedVisibility(
                    visible = showSelectionMenu,
                    enter = slideInVertically(
                        animationSpec = tween(durationMillis = 500),
                        initialOffsetY = { 3 * it / 5 }
                    ),
                    exit = slideOutVertically(
                        animationSpec = tween(durationMillis = 300),
                        targetOffsetY = { it + 200 }
                    ) + fadeOut(animationSpec = tween(300))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(bottom = 20.dp),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .background(color = Color.White, shape = RoundedCornerShape(15.dp)),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier
                                    .size(width = 100.dp, height = 50.dp)
                                    .clickable {
                                        showSelectionMenu = false
                                        onSelectImages?.run { onSelectImages(false) }
                                        selectedImages.clear()
                                    },
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Cancel Selection",
                                    tint = Color.Black
                                )
                                Text(
                                    text = stringResource(id = R.string.cancel_selections),
                                    fontSize = 14.sp
                                )
                            }

                            Column(
                                modifier = Modifier
                                    .size(width = 100.dp, height = 50.dp)
                                    .clickable {
                                        showSelectionMenu = false
                                        callback(selectedImages.toList())
                                        selectedImages.clear()
                                    },
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete sample",
                                    tint = colorResource(id = R.color.red)
                                )
                                Text(
                                    text = stringResource(id = R.string.delete),
                                    fontSize = 14.sp,
                                    color = colorResource(id = R.color.red)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}