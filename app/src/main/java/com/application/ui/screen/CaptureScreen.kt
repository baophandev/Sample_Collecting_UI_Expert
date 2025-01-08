package com.application.ui.screen

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.application.R
import com.application.ui.component.FullScreenImage
import com.application.ui.component.PhotoBottomSheetContent
import com.application.ui.viewmodel.CaptureViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaptureScreen(
    viewModel: CaptureViewModel = hiltViewModel(),
    popBackToStage: () -> Unit,
    navigateToCreateSample: (String, Uri) -> Unit
) {
    val context = LocalContext.current
    val controller = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(CameraController.IMAGE_CAPTURE)
        }
    }

    val scope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.Hidden,
            skipHiddenState = false
        ),
    )

    val sampleImages = viewModel.sampleImages
    var activeImageIdx by remember { mutableStateOf<Int?>(null) }
    var capturing by remember { mutableStateOf(false) }
    var sheetPeakHeight by remember { mutableStateOf(0.dp) }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = sheetPeakHeight,
        sheetContent = {
            PhotoBottomSheetContent(
                uris = sampleImages,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 20.dp),
                emptyTextAlignment = Alignment.TopCenter,
                onPhotoPress = {
                    if (it < sampleImages.size) {
                        scope.launch {
                            scaffoldState.bottomSheetState.hide()
                            sheetPeakHeight = 0.dp
                        }
                        activeImageIdx = it
                    }
                },
                onPhotosSelecting = { isSelecting ->
                    if (isSelecting &&
                        scaffoldState.bottomSheetState.currentValue != SheetValue.Expanded
                    ) {
                        sheetPeakHeight = 200.dp
                        scope.launch { scaffoldState.bottomSheetState.expand() }
                    }
                },
                onPhotosDeleted = { removeList ->
                    removeList.forEach { uri ->
                        if (deleteImage(context, uri))
                            sampleImages.remove(uri)
                    }
                }
            )
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (activeImageIdx == null)
                CameraPreview(controller = controller, modifier = Modifier.fillMaxSize())

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(15.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        modifier = Modifier.size(50.dp),
                        onClick = popBackToStage
                    ) {
                        Icon(
                            modifier = Modifier.fillMaxSize(),
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    IconButton(
                        modifier = Modifier.size(50.dp),
                        onClick = {
                            controller.cameraSelector =
                                if (controller.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA)
                                    CameraSelector.DEFAULT_FRONT_CAMERA
                                else CameraSelector.DEFAULT_BACK_CAMERA
                        }
                    ) {
                        Icon(
                            modifier = Modifier.fillMaxSize(),
                            painter = painterResource(id = R.drawable.ic_switch_camera),
                            contentDescription = "Switch camera",
                            tint = Color.White
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 20.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                val shape = RoundedCornerShape(percent = 50)

                IconButton(
                    modifier = Modifier
                        .size(100.dp)
                        .padding(10.dp)
                        .background(Color.White, shape),
                    onClick = {
                        scope.launch {
                            capturing = true

                            takePhoto(
                                context = context,
                                controller = controller,
                                onPhotoTaken = sampleImages::add
                            )

                            delay(300)
                            capturing = false
                        }
                    }
                ) {}

                Box(
                    modifier = Modifier.fillMaxWidth(.9f),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        IconButton(
                            modifier = Modifier
                                .size(70.dp)
                                .padding(10.dp)
                                .background(Color.White, shape),
                            onClick = {
                                scope.launch {
                                    if (sampleImages.isEmpty()) {
                                        scaffoldState.bottomSheetState.partialExpand()
                                        sheetPeakHeight = 200.dp
                                    } else scaffoldState.bottomSheetState.expand()
                                    Log.d(
                                        "CaptureScreen",
                                        "${scaffoldState.bottomSheetState.currentValue}"
                                    )
                                }
                            }
                        ) {
                            val lastImage = sampleImages.lastOrNull()
                            if (lastImage != null) {
                                AsyncImage(
                                    model = ImageRequest.Builder(context)
                                        .data(lastImage)
                                        .error(R.drawable.ic_gallery)
                                        .placeholder(R.drawable.ic_gallery)
                                        .build(),
                                    modifier = Modifier.fillMaxSize(),
                                    contentDescription = "Open gallery",
                                    contentScale = ContentScale.FillBounds
                                )
                            } else
                                Icon(
                                    modifier = Modifier.size(20.dp),
                                    painter = painterResource(id = R.drawable.ic_gallery),
                                    contentDescription = "Open gallery",
                                    tint = Color.Black
                                )
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = capturing,
                enter = fadeIn(animationSpec = tween(200), initialAlpha = .8f),
                exit = scaleOut(animationSpec = tween(200), targetScale = .8f) + fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(255, 255, 255, 153))
                )
            }

            if (activeImageIdx != null) {
                val uri = sampleImages[activeImageIdx!!]

                FullScreenImage(
                    uri = uri,
                    onDismiss = { activeImageIdx = null }
                )

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            modifier = Modifier.size(width = 120.dp, height = 50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent
                            ),
                            border = BorderStroke(1.dp, colorResource(id = R.color.red)),
                            shape = RoundedCornerShape(25.dp),
                            onClick = {
                                if (deleteImage(context, uri)) {
                                    sampleImages.removeAt(activeImageIdx!!)
                                    activeImageIdx = null
                                }
                            }
                        ) {
                            Text(
                                text = stringResource(id = R.string.delete),
                                fontSize = 16.sp,
                                color = colorResource(id = R.color.red)
                            )
                        }

                        Button(
                            modifier = Modifier.size(width = 120.dp, height = 50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent
                            ),
                            border = BorderStroke(1.dp, colorResource(id = R.color.sky_blue)),
                            shape = RoundedCornerShape(25.dp),
                            onClick = {
                                val image = sampleImages[activeImageIdx!!]
                                navigateToCreateSample(viewModel.stageId, image)
                            }
                        ) {
                            Text(
                                text = stringResource(id = R.string.save_button),
                                fontSize = 16.sp,
                                color = colorResource(id = R.color.sky_blue)
                            )
                        }
                    }
                }
            }
        }
    }
}

fun deleteImage(context: Context, imageUri: Uri): Boolean {
    val numDeletedFile = context.contentResolver.delete(imageUri, null)
    return numDeletedFile != 0
}

@Composable
private fun CameraPreview(
    controller: LifecycleCameraController,
    modifier: Modifier = Modifier
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    AndroidView(
        factory = {
            PreviewView(it).apply {
                this.controller = controller
                controller.bindToLifecycle(lifecycleOwner)
            }
        },
        modifier = modifier
    )
}

private fun takePhoto(
    context: Context,
    controller: LifecycleCameraController,
    onPhotoTaken: (Uri) -> Unit
) {
    val tag = "CaptureScreen"
    val filenameFormat = "yyyy-MM-dd-HH-mm-ss-SSS"

    // Create time stamped name and MediaStore entry.
    val name = SimpleDateFormat(filenameFormat, Locale.US)
        .format(System.currentTimeMillis())
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, name)
        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Sample-Collecting")
    }

    // Create output options object which contains file + metadata
    val outputOptions = ImageCapture.OutputFileOptions
        .Builder(
            context.contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )
        .build()

    // Set up image capture listener, which is triggered after photo has been taken
    controller.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onError(exc: ImageCaptureException) {
                Log.e(tag, "Photo capture failed: ${exc.message}", exc)
            }

            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                output.savedUri?.let { onPhotoTaken(it) }
            }
        }
    )
}