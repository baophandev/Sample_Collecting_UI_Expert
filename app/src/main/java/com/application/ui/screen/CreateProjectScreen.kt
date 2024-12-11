package com.application.ui.screen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.application.R
import com.application.ui.component.BotNavigationBar
import com.application.ui.component.CustomButton
import com.application.ui.component.CustomDatePicker
import com.application.ui.component.CustomSnackBarHost
import com.application.ui.component.CustomTextField
import com.application.ui.component.FieldToList
import com.application.ui.component.TopBar
import com.application.ui.viewmodel.CreateProjectViewModel
import com.sc.library.utility.validate.RegexValidation

@Composable
fun CreateProjectScreen(
    viewModel: CreateProjectViewModel = hiltViewModel(),
    navigateToLogin: () -> Unit,
    navigateToHome: (String?) -> Unit,
    navigateToWorkersQuestionScreen: () -> Unit,
    navigateToExpertChatScreen: () -> Unit
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }
    val pickPictureLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { imageUri -> imageUri?.let(viewModel::updateThumbnail) }

    if (state.error != null) {
        val error = stringResource(id = state.error!!)
        LaunchedEffect(key1 = "showSnackBar") {
            val result = snackBarHostState.showSnackbar(
                message = error,
                withDismissAction = true,
                duration = SnackbarDuration.Short
            )
            if (result == SnackbarResult.Dismissed) {
                viewModel.gotError()
            }
        }
    }
    if (state.loading) LoadingScreen(text = stringResource(id = R.string.creating_project))
    else {
        Scaffold(
            modifier = Modifier,
            snackbarHost = {
                CustomSnackBarHost(
                    snackBarHostState = snackBarHostState,
                    dismissAction = {
                        IconButton(
                            modifier = Modifier
                                .padding(0.dp)
                                .size(50.dp),
                            onClick = viewModel::gotError
                        ) {
                            Icon(
                                modifier = Modifier.fillMaxSize(),
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Close"
                            )
                        }
                    }
                )
            },
            topBar = {
                TopBar(title = R.string.create_project, signOutClicked = navigateToLogin)
            },
            bottomBar = {
                BotNavigationBar (
                    onWorkersQuestionClick = navigateToWorkersQuestionScreen,
                    onExpertChatsClick = navigateToExpertChatScreen
                ) {
                    IconButton(
                        modifier = Modifier.size(50.dp),

                        onClick = { navigateToHome(null) }
                    ) {
                        Icon(
                            modifier = Modifier.fillMaxSize(.60f),
                            painter = painterResource(id = R.drawable.ic_home),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceAround
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(.95f)
                        .height(180.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.LightGray
                    ),
                    onClick = { pickPictureLauncher.launch("image/*") }
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        val thumbnailUri = state.thumbnail
                        if (thumbnailUri != null) {
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(thumbnailUri).build(),
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop,
                                contentDescription = null
                            )
                        } else {
                            Icon(
                                modifier = Modifier
                                    .fillMaxSize(.3f),
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Icon",
                                tint = colorResource(id = R.color.main_green)
                            )
                        }
                    }
                }

                CustomTextField(
                    modifier = Modifier
                        .fillMaxWidth(.95f)
                        .height(60.dp),
                    placeholder = { Text(text = stringResource(id = R.string.add_title)) },
                    singleLine = true,
                    value = state.name,
                    onValueChange = viewModel::updateTitle
                )

                CustomTextField(
                    modifier = Modifier
                        .fillMaxWidth(.95f)
                        .height(100.dp),
                    placeholder = { Text(text = stringResource(id = R.string.add_description)) },
                    value = state.description,
                    onValueChange = viewModel::updateDescription
                )

                Row(
                    modifier = Modifier.fillMaxWidth(.95f),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CustomDatePicker(
                        fieldName = stringResource(id = R.string.start_date),
                        modifier = Modifier.width(160.dp)
                    ) { viewModel.updateDate(date = it, isStartDate = true) }
                    CustomDatePicker(
                        fieldName = stringResource(id = R.string.end_date),
                        modifier = Modifier.width(160.dp)
                    ) { viewModel.updateDate(date = it, isStartDate = false) }
                }

                FieldToList(
                    fieldDataList = state.memberIds,
                    textValidator = { email -> email.contains(RegexValidation.EMAIL) },
                    onAddField = {},
                    onRemoveField = {}
                )

                CustomButton(
                    modifier = Modifier.fillMaxWidth(.95f),
                    text = stringResource(id = R.string.submit),
                    textSize = 20.sp,
                    background = colorResource(id = R.color.main_green),
                    border = BorderStroke(0.dp, Color.Transparent),
                    action = {
                        viewModel.submit(successHandler = navigateToHome)
                    }
                )
            }
        }
    }
}

