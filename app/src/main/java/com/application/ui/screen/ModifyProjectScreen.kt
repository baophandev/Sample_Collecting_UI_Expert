package com.application.ui.screen

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.application.R
import com.application.android.utility.validate.RegexValidation
import com.application.constant.UiStatus
import com.application.ui.component.BotNavigationBar
import com.application.ui.component.CustomDatePicker
import com.application.ui.component.CustomSnackBarHost
import com.application.ui.component.CustomTextField
import com.application.ui.component.FieldToList
import com.application.ui.component.TopBar
import com.application.ui.viewmodel.ModifyProjectViewModel

@Composable
fun ModifyProjectScreen(
    viewModel: ModifyProjectViewModel = hiltViewModel(),
    projectId: String,
    popBackToLogin: () -> Unit,
    popBackToHome: () -> Unit,
    postUpdatedHandler: (Boolean) -> Unit,
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
        LaunchedEffect(key1 = state.error) {
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
    when (state.status) {
        UiStatus.INIT -> viewModel.loadProject(projectId)
        UiStatus.LOADING -> LoadingScreen(text = stringResource(id = R.string.loading))
        UiStatus.SUCCESS -> Scaffold(
            modifier = Modifier,
            snackbarHost = {
                CustomSnackBarHost(
                    snackBarHostState = snackBarHostState,
                    dismissAction = {
                        IconButton(
                            modifier = Modifier
                                .padding(end = 10.dp)
                                .size(30.dp),
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
            topBar = { TopBar(title = R.string.modify_project, signOutClicked = popBackToLogin) },
            bottomBar = {
                BotNavigationBar(
                    onWorkersQuestionClick = navigateToWorkersQuestionScreen,
                    onExpertChatsClick = navigateToExpertChatScreen
                ) {
                    IconButton(
                        modifier = Modifier.size(50.dp),
                        onClick = popBackToHome
                    ) {
                        Icon(
                            modifier = Modifier.fillMaxSize(.60f),
                            painter = painterResource(id = R.drawable.ic_home),
                            contentDescription = null,
                            tint = Color.White
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
                        .fillMaxWidth(.95f),
                    colors = CardDefaults.cardColors(
                        containerColor = colorResource(id = R.color.gray_color)
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 10.dp,
                            pressedElevation = 12.dp
                        ),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(
                            horizontal = 0.dp,
                            vertical = 0.dp
                        ),
                        onClick = { pickPictureLauncher.launch("image/*") }
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(colorResource(id = R.color.gray_100)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                modifier = Modifier
                                    .fillMaxSize(.3f),
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Icon",
                                tint = colorResource(id = R.color.main_green)
                            )
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(state.project?.thumbnail)
                                    .build(),
                                modifier = Modifier
                                    .fillMaxSize()
                                    .zIndex(0f), // Place background image below
                                contentScale = ContentScale.Crop,
                                contentDescription = "Thumbnail",
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
                    value = state.project?.name ?: "Khong co ten du an",
                    onValueChange = viewModel::updateProjectName
                )
                CustomTextField(
                    modifier = Modifier
                        .fillMaxWidth(.95f)
                        .height(100.dp),
                    placeholder = { Text(text = stringResource(id = R.string.sample_description_default)) },
                    value = state.project?.description ?: "Khong co mo ta",
                    onValueChange = viewModel::updateDescription
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth(.95f),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CustomDatePicker(
                        fieldName = stringResource(id = R.string.start_date),
                        initValue = state.project?.startDate,
                        modifier = Modifier.width(160.dp)
                    ) { viewModel.updateDate(date = it, isStartDate = true) }
                    CustomDatePicker(
                        fieldName = stringResource(id = R.string.end_date),
                        initValue = state.project?.endDate,
                        modifier = Modifier.width(160.dp)
                    ) { viewModel.updateDate(date = it, isStartDate = false) }
                }

                if (state.memberUsernames.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .fillMaxWidth(.95f)
                            .height(120.dp)
                            .border(
                                width = 0.dp,
                                Color.LightGray,
                                shape = RoundedCornerShape(20.dp)
                            )
                            .background(colorResource(id = R.color.gray_100)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(id = R.string.no_members),
                            textAlign = TextAlign.Center,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Light
                        )
                    }
                } else {
                    FieldToList(
                        fieldDataList = state.memberUsernames,
                        textValidator = { email ->
                            email.contains(RegexValidation.EMAIL)
                        },
                        onAddField = {},
                        onRemoveField = {}
                    )
                }
                // Luu thong tin sau chinh sua
                Button(
                    modifier = Modifier
                        .fillMaxWidth(.95f)
                        .height(40.dp),
                    enabled = state.isUpdated,
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 6.dp,
                        pressedElevation = 8.dp
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = R.color.main_green),
                        contentColor = colorResource(id = R.color.black)
                    ),
                    onClick = {
                        viewModel.submit(successHandler = postUpdatedHandler)
                    }
                ) {
                    Text(color = Color.White, text = stringResource(id = R.string.save_button))
                }
            }
        }


        UiStatus.ERROR -> Toast.makeText(context, state.error!!, Toast.LENGTH_LONG).show()
    }
}
