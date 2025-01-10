package com.application.ui.component

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
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

@Composable
@OptIn(ExperimentalFoundationApi::class)
fun PhotoBottomSheetContent(
    modifier: Modifier = Modifier,
    state: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
    enableFunctionalMenu: Boolean = true,
    uris: List<Uri>,
    emptyTextAlignment: Alignment = Alignment.Center,
    onPhotoPress: (Int) -> Unit,
    onPhotosSelecting: (Boolean) -> Unit,
    onPhotosDeleted: (List<Uri>) -> Unit,
    isRefreshing: Boolean = false,
    onRefresh: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val haptics = LocalHapticFeedback.current

    var showSelectionMenu by remember { mutableStateOf(false) }
    val selectedImages = remember { mutableStateListOf<Uri>() }

    if (uris.isEmpty()) {
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
        ContainerBox(isRefreshing = isRefreshing, onRefresh = onRefresh) {
            LazyVerticalStaggeredGrid(
                modifier = modifier,
                state = state,
                columns = StaggeredGridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalItemSpacing = 16.dp,
                contentPadding = PaddingValues(16.dp)
            ) {
                itemsIndexed(
                    items = uris,
                    key = { idx, _ -> idx }
                ) { idx, uri ->
                    val onImageClick: () -> Unit = {
                        if (!showSelectionMenu) onPhotoPress(idx)
                        else {
                            if (uri in selectedImages)
                                selectedImages.remove(uri)
                            else selectedImages.add(uri)
                        }
                    }
                    val onImageLongClick: () -> Unit = {
                        onPhotosSelecting.let {
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            showSelectionMenu = true
                            onPhotosSelecting(true)
                            selectedImages.add(uri)
                        }
                    }

                    Box {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(uri)
                                .error(R.drawable.img_error)
                                .placeholder(R.drawable.img_placeholder)
                                .fallback(R.drawable.img_fallback)
                                .build(),
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .combinedClickable(
                                    onClick = onImageClick,
                                    onLongClick = if (enableFunctionalMenu) onImageLongClick else null
                                ),
                            contentDescription = null
                        )

                        if (uri in selectedImages) {
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

            if (enableFunctionalMenu) {
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
                                .padding(start = 5.dp, end = 5.dp, top = 10.dp, bottom = 10.dp)
                                .height(100.dp)
                                .background(
                                    color = Color.White,
                                    shape = RoundedCornerShape(15.dp)
                                ),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                colors = ButtonDefaults.buttonColors().copy(
                                    containerColor = Color.Transparent,
                                    contentColor = Color.Black
                                ),
                                onClick = {
                                    showSelectionMenu = false
                                    onPhotosSelecting(false)
                                    selectedImages.clear()
                                }
                            ) {
                                Column(
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
                            }

                            Button(
                                colors = ButtonDefaults.buttonColors().copy(
                                    containerColor = Color.Transparent,
                                    contentColor = colorResource(id = R.color.red)
                                ),
                                onClick = {
                                    showSelectionMenu = false
                                    onPhotosDeleted(selectedImages.toList())
                                    selectedImages.clear()
                                }
                            ) {
                                Column(
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
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ContainerBox(
    isRefreshing: Boolean = false,
    onRefresh: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val refreshState = rememberPullToRefreshState()

    if (onRefresh != null) PullToRefreshBox(
        state = refreshState,
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
        isRefreshing = isRefreshing,
        onRefresh = onRefresh
    ) {
        content()
    }
    else Box {
        content()
    }
}