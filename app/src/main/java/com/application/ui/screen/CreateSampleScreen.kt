package com.application.ui.screen

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.application.R
import com.application.constant.UiStatus
import com.application.ui.component.BlockField
import com.application.ui.component.CustomButton
import com.application.ui.component.DynamicField
import com.application.ui.component.LoadingScreen
import com.application.ui.viewmodel.CreateSampleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateSampleScreen(
    viewModel: CreateSampleViewModel = hiltViewModel(),
    stageId: String,
    newSample: Pair<String, Uri>,
    navigateToCapture: (String?) -> Unit,
    navigateToHome: () -> Unit
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()

    val cannotCreateSample = stringResource(id = R.string.create_sample_cancel)

    val snackBarHostState = remember { SnackbarHostState() }
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = SheetState(
            skipPartiallyExpanded = false,
            density = LocalDensity.current,
            initialValue = SheetValue.PartiallyExpanded,
            skipHiddenState = true
        )
    )

    when (state.status) {
        UiStatus.INIT -> viewModel.loadForm(stageId)
        UiStatus.LOADING -> LoadingScreen(text = stringResource(id = R.string.loading))
        UiStatus.ERROR -> TODO()
        UiStatus.SUCCESS -> Box {
            BottomSheetScaffold(
                scaffoldState = scaffoldState,
                sheetPeekHeight = 600.dp,
                sheetContent = {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 15.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {
                        itemsIndexed(
                            items = state.answers,
                            key = { index, _ -> index }
                        ) { index, data ->
                            Spacer(modifier = Modifier.size(4.dp))
                            BlockField(
                                fieldName = data.field.name,
                                onValueChange = { newValue ->
                                    viewModel.updateAnswer(index, newValue)
                                }
                            )
                            Spacer(modifier = Modifier.size(4.dp))
                        }

                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    modifier = Modifier
                                        .padding(0.dp)
                                        .size(50.dp),
                                    onClick = { TODO("Add a dynamic field") }
                                ) {
                                    Icon(
                                        modifier = Modifier.fillMaxSize(),
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Add flex field",
                                        tint = colorResource(id = R.color.main_green)
                                    )
                                }
                            }
                        }

                        itemsIndexed(
                            items = state.dynamicFields,
                            key = { _, field -> field.id }
                        ) { index, data ->
                            Spacer(modifier = Modifier.size(4.dp))
                            DynamicField(
                                fieldName = data.name,
                                onFieldNameChange = { fieldName ->
                                    viewModel.updateDynamicFieldName(index, fieldName)
                                },
                                fieldValue = data.value,
                                onFieldValueChange = { fieldValue ->
                                    viewModel.updateDynamicFieldValue(index, fieldValue)
                                },
                                onDeleteClicked = {
                                    viewModel.deleteDynamicField(index)
                                }
                            )
                            Spacer(modifier = Modifier.size(4.dp))
                        }
                    }
                },
                snackbarHost = {
                    SnackbarHost(hostState = snackBarHostState) {
                        Snackbar(
                            containerColor = colorResource(id = R.color.red),
                            dismissAction = {
                                IconButton(
                                    modifier = Modifier.size(50.dp),
                                    onClick = viewModel::gotError
                                ) {
                                    Icon(
                                        modifier = Modifier.fillMaxSize(),
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Close"
                                    )
                                }
                            }
                        ) {
                            val visuals = snackBarHostState.currentSnackbarData?.visuals
                            val message = visuals?.message
                            Text(text = message ?: "")
                        }
                    }
                }
            ) { _ ->
                Box(modifier = Modifier.fillMaxSize()) {
                    AsyncImage(
                        model = ImageRequest.Builder(context).data(newSample.second).build(),
                        contentDescription = "Sample Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.TopCenter,
                    )

                    Box(
                        modifier = Modifier
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        contentAlignment = Alignment.TopStart
                    ) {
                        CustomButton(
                            text = stringResource(id = R.string.recapture_button),
                            textSize = 14.sp,
                            textColor = Color.Black,
                            background = Color.White,
                            border = BorderStroke(1.dp, Color.LightGray),
                            action = { navigateToCapture(null) }
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
                CustomButton(
                    modifier = Modifier
                        .fillMaxWidth(.7f),
                    text = stringResource(id = R.string.save_button),
                    textSize = 16.sp,
                    background = colorResource(id = R.color.main_green),
                    border = BorderStroke(0.dp, Color.Transparent),
                    action = {
                        viewModel.submitSample(
                            stageId = stageId,
                            sampleImage = newSample,
                            result = navigateToCapture,
                            isCancelled = {
                                Toast.makeText(context, cannotCreateSample, Toast.LENGTH_SHORT)
                                    .show()
                                navigateToHome()
                            }
                        )
                    }
                )
            }

            if (state.error != null) {
                val error = stringResource(id = state.error!!)
                LaunchedEffect(key1 = "showSnackBar") {
                    val result = snackBarHostState.showSnackbar(
                        message = error,
                        withDismissAction = true,
                        duration = SnackbarDuration.Short
                    )
                    if (result == SnackbarResult.Dismissed) viewModel.gotError()
                }
            }
        }
    }
}