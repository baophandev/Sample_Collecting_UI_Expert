package com.application.ui.screen

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.application.R
import com.application.constant.UiStatus
import com.application.ui.component.CustomButton
import com.application.ui.component.FullScreenImage
import com.application.ui.component.LoadingScreen
import com.application.ui.component.NameAndValueField
import com.application.ui.component.PhotoBottomSheetContent
import com.application.ui.component.TopNavigationBar
import com.application.ui.viewmodel.StageDetailViewModel

internal enum class StageSwitchState { DETAIL, PHOTOS }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StageDetailScreen(
    viewModel: StageDetailViewModel = hiltViewModel(),
    isProjectOwner: Boolean,
    stageId: String,
    thumbnailUri: Uri? = null,
    navigateToModifyStage: (String) -> Unit,
    navigateToCapture: () -> Unit,
    navigateToDetail: () -> Unit
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
    var switch by remember { mutableStateOf(StageSwitchState.DETAIL) }
    var showSampleData by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showAddPhoto by remember { mutableStateOf(true) }
    var activeImageIdx by remember { mutableStateOf<Int?>(null) }

    if (showDeleteDialog) {
        AlertDialog(
            title = {
                Text(text = stringResource(id = R.string.delete_stage))
            },
            text = {
                Text(text = stringResource(id = R.string.delete_stage_description))
            },
            onDismissRequest = { showDeleteDialog = false },
            confirmButton = {
                CustomButton(
                    text = stringResource(id = R.string.delete_this_project),
                    textSize = 14.sp,
                    background = colorResource(id = R.color.red),
                    border = BorderStroke(0.dp, Color.Transparent)
                ) {
                    viewModel.deleteStage(
                        projectId = stageId,
                        stageId = stageId,
                        successHandler = navigateToDetail
                    )
                }
            },
            dismissButton = {
                CustomButton(
                    text = stringResource(id = R.string.cancel),
                    textSize = 14.sp,
                    textColor = Color.Black,
                    background = Color.White,
                    border = BorderStroke(0.dp, Color.Transparent)
                ) { showDeleteDialog = false }
            }
        )
    }

    when (state.status) {
        UiStatus.INIT -> viewModel.loadStage(stageId)
        UiStatus.LOADING -> LoadingScreen(text = stringResource(id = R.string.loading))
        UiStatus.SUCCESS -> {
            Box {
                BottomSheetScaffold(
                    scaffoldState = scaffoldState,
                    sheetPeekHeight = if (activeImageIdx == null) 600.dp else 0.dp,
                    sheetContent = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            CustomButton(
                                text = stringResource(id = R.string.detail),
                                textSize = 16.sp,
                                textColor = if (switch == StageSwitchState.DETAIL)
                                    Color.White else Color.Black,
                                background = if (switch == StageSwitchState.DETAIL)
                                    MaterialTheme.colorScheme.primary else Color.White,
                                border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                                action = { switch = StageSwitchState.DETAIL }
                            )
                            CustomButton(
                                text = stringResource(id = R.string.photos),
                                textSize = 16.sp,
                                textColor = if (switch == StageSwitchState.PHOTOS)
                                    Color.White else Color.Black,
                                background = if (switch == StageSwitchState.PHOTOS)
                                    MaterialTheme.colorScheme.primary else Color.White,
                                border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                                action = { switch = StageSwitchState.PHOTOS }
                            )
                        }

                        when (switch) {
                            StageSwitchState.DETAIL -> {
                                Column(
                                    modifier = Modifier.padding(
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
                                        text = state.stage?.description
                                            ?: stringResource(id = R.string.default_project_description),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.W400
                                    )
                                }
                            }

                            StageSwitchState.PHOTOS -> {
                                Column(
                                    modifier = Modifier
                                        .padding(horizontal = 15.dp, vertical = 5.dp)
                                        .fillMaxSize()
                                ) {
                                    PhotoBottomSheetContent(
                                        uriList = state.imageUris.map { pair -> pair.second },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .fillMaxHeight(if (state.imageUris.isEmpty()) .2f else .9f),
                                        onPhotoPress = { imageIdx ->
                                            if (imageIdx < state.imageUris.size) {
                                                activeImageIdx = imageIdx
                                                val sampleId = state.imageUris[imageIdx].first
                                                viewModel.loadSampleData(
                                                    projectId = stageId,
                                                    stageId = stageId,
                                                    sampleId = sampleId
                                                )
                                            }
                                        },
                                        onSelectImages = if (isProjectOwner) { isSelecting ->
                                            showAddPhoto = !isSelecting
                                        } else null,
                                        onDeleteImages = if (isProjectOwner) { removeList ->
                                            removeList.forEach { uri ->
                                                state.imageUris.find { it.second == uri }
                                                    ?.let { sample ->
//                                                        viewModel.deleteSample(
//                                                            stageId,
//                                                            stage.first,
//                                                            sample.first
//                                                        )
                                                        state.imageUris.remove(sample)
                                                    }
                                            }
                                        } else null
                                    )
                                }
                            }
                        }
                    }
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        if (thumbnailUri != null) {
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(thumbnailUri)
                                    .build(),
                                modifier = Modifier
                                    .height(300.dp)
                                    .fillMaxWidth(),
                                contentDescription = "Thumbnail",
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Image(
                                modifier = Modifier
                                    .height(300.dp)
                                    .fillMaxWidth(),
                                painter = painterResource(id = R.drawable.sample_default),
                                contentDescription = "Default Thumbnail",
                                contentScale = ContentScale.FillBounds
                            )
                        }
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            TopNavigationBar(
                                backAction = navigateToDetail
                            ) {
                                if (isProjectOwner) {
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
                                text = state.stage?.name ?: "Title",
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
                    when (switch) {
                        StageSwitchState.DETAIL -> {
                            if (isProjectOwner) {
                                CustomButton(
                                    modifier = Modifier.fillMaxWidth(.7f),
                                    text = stringResource(id = R.string.modify),
                                    textSize = 16.sp,
                                    background = MaterialTheme.colorScheme.primary,
                                    border = BorderStroke(0.dp, Color.Transparent),
                                    action = {
                                        navigateToModifyStage(stageId)
                                    }
                                )
                            }
                        }

                        StageSwitchState.PHOTOS -> {
                            if (showAddPhoto) {
                                CustomButton(
                                    modifier = Modifier.fillMaxWidth(.7f),
                                    text = stringResource(id = R.string.add_photo),
                                    textSize = 16.sp,
                                    background = MaterialTheme.colorScheme.primary,
                                    border = BorderStroke(0.dp, Color.Transparent),
                                    action = navigateToCapture
                                )
                            }
                        }
                    }

                    if (activeImageIdx != null) {
                        if (activeImageIdx!! < state.imageUris.size) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.White)
                            ) {
                                FullScreenImage(
                                    uri = state.imageUris[activeImageIdx!!].second,
                                    onDismiss = { activeImageIdx = null },
                                    onTapGesture = { showSampleData = !showSampleData }
                                )
                            }
                            state.sample?.let { sample ->
                                AnimatedVisibility(
                                    visible = (showSampleData),
                                    enter = fadeIn(animationSpec = tween(400)),
                                    exit = fadeOut(animationSpec = tween(400))
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color(0, 0, 0, 153)),
                                        contentAlignment = Alignment.BottomStart
                                    ) {
                                        Column(modifier = Modifier.padding(vertical = 10.dp)) {
                                            NameAndValueField(
                                                modifier = Modifier
                                                    .padding(horizontal = 15.dp),
                                                fieldName = stringResource(id = R.string.written_by),
                                                fieldNameSize = 18.sp,
                                                fieldValue = sample.writtenBy!!,
                                                fieldValueSize = 18.sp
                                            )
                                            sample.data?.toList()?.let { data ->
                                                LazyColumn(
                                                    modifier = Modifier
                                                        .padding(horizontal = 15.dp)
                                                        .fillMaxWidth()
                                                        .wrapContentHeight(),
                                                    horizontalAlignment = Alignment.Start,
                                                    verticalArrangement = Arrangement.Bottom
                                                ) {
                                                    items(data) { value ->
                                                        Spacer(modifier = Modifier.size(10.dp))
                                                        NameAndValueField(
                                                            fieldName = value.first + ": ",
                                                            fieldNameSize = 18.sp,
                                                            fieldValue = value.second,
                                                            fieldValueSize = 18.sp
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } else activeImageIdx = null
                    }
                }
            }
        }

        UiStatus.ERROR -> {}
    }
}