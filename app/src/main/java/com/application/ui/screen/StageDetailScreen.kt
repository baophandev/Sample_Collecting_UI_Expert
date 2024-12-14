package com.application.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.application.R
import com.application.constant.UiStatus
import com.application.data.entity.Sample
import com.application.ui.component.CustomButton
import com.application.ui.component.PhotoBottomSheetContent
import com.application.ui.component.TopNavigationBar
import com.application.ui.viewmodel.StageDetailViewModel

private enum class StageTab { DETAIL, PHOTOS }

/**
 * @param navigateToModifyStage (projectId, stageId) -> Unit
 * @param navigateToSampleDetail (sampleId) -> Unit
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StageDetailScreen(
    viewModel: StageDetailViewModel = hiltViewModel(),
    popBackToDetail: (Boolean) -> Unit,
    navigateToModifyStage: (String, String) -> Unit,
    navigateToCapture: (String) -> Unit,
    navigateToSampleDetail: (String) -> Unit
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()

    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = SheetState(
            skipPartiallyExpanded = false,
            density = LocalDensity.current,
            initialValue = SheetValue.PartiallyExpanded,
            skipHiddenState = true
        )
    )
    var currentTab by remember { mutableStateOf(StageTab.DETAIL) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showAddPhoto by remember { mutableStateOf(true) }

    DeleteStageAlertDialog(
        show = showDeleteDialog,
        onDismissRequest = { showDeleteDialog = false },
        onConfirmButtonClick = {
            showDeleteDialog = false
            viewModel.deleteStage(successHandler = popBackToDetail)
        }
    )

    when (state.status) {
        UiStatus.LOADING -> LoadingScreen(text = stringResource(id = R.string.loading))
        UiStatus.SUCCESS -> {
            val sampleLazyPagingItems = viewModel.sampleFlow.collectAsLazyPagingItems()

            Box {
                BottomSheetScaffold(
                    scaffoldState = scaffoldState,
                    sheetPeekHeight = 600.dp,
                    sheetContent = {
                        TabButtons(tab = currentTab) { newTab -> currentTab = newTab }

                        when (currentTab) {
                            StageTab.DETAIL -> DetailTab(
                                description = state.stage?.description
                            )

                            StageTab.PHOTOS -> PhotoTab(
                                isProjectOwner = viewModel.isProjectOwner(),
                                pagingItems = sampleLazyPagingItems,
                                onPhotoPress = { imageIdx ->
                                    sampleLazyPagingItems[imageIdx]?.let { sample ->
//                                        activeImageIdx = imageIdx
//                                        viewModel.loadSampleData(sampleId = sample.id)
                                        navigateToSampleDetail(sample.id)
                                    }
                                },
                                onImagesSelected = { isSelecting -> showAddPhoto = !isSelecting },
                                onImagesDeleted = { deletedIds ->
                                    deletedIds.forEach(viewModel::deleteSample)
                                }
                            )
                        }
                    }
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        if (state.thumbnail != null)
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(state.thumbnail)
                                    .build(),
                                modifier = Modifier
                                    .height(300.dp)
                                    .fillMaxWidth(),
                                contentDescription = "Thumbnail",
                                contentScale = ContentScale.Crop
                            )
                        else
                            Image(
                                modifier = Modifier
                                    .height(300.dp)
                                    .fillMaxWidth(),
                                painter = painterResource(id = R.drawable.ic_launcher_background),
                                contentDescription = "Default Thumbnail",
                                contentScale = ContentScale.FillBounds
                            )
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            TopNavigationBar(
                                backAction = {
                                    viewModel.updateStageInDetail(successHandler = popBackToDetail)
                                }
                            ) {
                                if (viewModel.isProjectOwner()) {
                                    DropdownMenuItem(
                                        leadingIcon = {
                                            Icon(
                                                modifier = Modifier.size(20.dp),
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Delete stage",
                                                tint = colorResource(id = R.color.red)
                                            )
                                        },
                                        text = {
                                            Text(
                                                color = colorResource(id = R.color.red),
                                                text = stringResource(id = R.string.delete_stage)
                                            )
                                        },
                                        onClick = { showDeleteDialog = true }
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.size(40.dp))
                            Text(
                                text = state.stage?.name ?: stringResource(R.string.unknown_stage),
                                fontSize = 30.sp,
                                color = Color.White
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 20.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    when (currentTab) {
                        StageTab.DETAIL -> if (viewModel.isProjectOwner()) {
                            CustomButton(
                                modifier = Modifier.fillMaxWidth(.7f),
                                text = stringResource(id = R.string.modify),
                                textSize = 16.sp,
                                background = MaterialTheme.colorScheme.primary,
                                border = BorderStroke(0.dp, Color.Transparent),
                                action = {
                                    val currentStage = state.stage!!
                                    navigateToModifyStage(
                                        currentStage.projectOwnerId,
                                        currentStage.id
                                    )
                                }
                            )
                        }

                        StageTab.PHOTOS -> if (showAddPhoto) {
                            CustomButton(
                                modifier = Modifier.fillMaxWidth(.7f),
                                text = stringResource(id = R.string.add_photo),
                                textSize = 16.sp,
                                background = MaterialTheme.colorScheme.primary,
                                border = BorderStroke(0.dp, Color.Transparent),
                                action = { navigateToCapture(state.stage!!.id) }
                            )
                        }
                    }
                }
            }
        }

        else -> {}
    }
}

@Composable
private fun DeleteStageAlertDialog(
    show: Boolean,
    onDismissRequest: () -> Unit,
    onConfirmButtonClick: () -> Unit
) {
    if (show) {
        AlertDialog(
            title = {
                Text(text = stringResource(id = R.string.delete_stage))
            },
            text = {
                Text(text = stringResource(id = R.string.delete_stage_description))
            },
            onDismissRequest = onDismissRequest,
            confirmButton = {
                CustomButton(
                    text = stringResource(id = R.string.delete_this_project),
                    textSize = 14.sp,
                    background = colorResource(id = R.color.red),
                    border = BorderStroke(0.dp, Color.Transparent),
                    action = onConfirmButtonClick
                )
            },
            dismissButton = {
                CustomButton(
                    text = stringResource(id = R.string.cancel),
                    textSize = 14.sp,
                    textColor = Color.Black,
                    background = Color.White,
                    border = BorderStroke(0.dp, Color.Transparent),
                    action = onDismissRequest
                )
            }
        )
    }
}

@Composable
private fun TabButtons(
    modifier: Modifier = Modifier,
    tab: StageTab,
    onTabChange: (StageTab) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        CustomButton(
            text = stringResource(id = R.string.detail),
            textSize = 16.sp,
            textColor = if (tab == StageTab.DETAIL)
                Color.White else Color.Black,
            background = if (tab == StageTab.DETAIL)
                MaterialTheme.colorScheme.primary else Color.White,
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
            action = { onTabChange(StageTab.DETAIL) }
        )
        CustomButton(
            text = stringResource(id = R.string.photos),
            textSize = 16.sp,
            textColor = if (tab == StageTab.PHOTOS)
                Color.White else Color.Black,
            background = if (tab == StageTab.PHOTOS)
                MaterialTheme.colorScheme.primary else Color.White,
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
            action = { onTabChange(StageTab.PHOTOS) }
        )
    }
}

@Composable
private fun PhotoTab(
    isProjectOwner: Boolean,
    pagingItems: LazyPagingItems<Sample>,
    onPhotoPress: (Int) -> Unit,
    onImagesSelected: (Boolean) -> Unit,
    onImagesDeleted: (List<String>) -> Unit
) {
    val state = rememberLazyStaggeredGridState()
    val items = pagingItems.itemSnapshotList.items

    LaunchedEffect(state.isScrollInProgress) {

    }

    Column(
        modifier = Modifier
            .padding(horizontal = 15.dp, vertical = 5.dp)
            .fillMaxSize()
    ) {
        PhotoBottomSheetContent(
            uris = items.map { Pair(it.id, it.image) },
            state = state,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(if (items.isEmpty()) .2f else .9f),
            onPhotoPress = onPhotoPress,
            onPhotosSelected = if (isProjectOwner) onImagesSelected else null,
            onPhotosDeleted = if (isProjectOwner) onImagesDeleted else null
        )
    }
}

@Composable
private fun DetailTab(
    modifier: Modifier = Modifier,
    description: String? = null
) {
    Column(
        modifier = modifier.padding(
            horizontal = 15.dp,
            vertical = 5.dp
        ),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = stringResource(id = R.string.detail),
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.W700
        )
        Text(
            modifier = Modifier.padding(horizontal = 10.dp),
            overflow = TextOverflow.Ellipsis,
            text = description ?: stringResource(id = R.string.default_project_description),
            fontSize = 16.sp,
            fontWeight = FontWeight.W400
        )
    }
}