package com.application.ui.screen

import android.widget.Toast
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.application.R
import com.application.constant.UiStatus
import com.application.ui.component.BotNavigationBar
import com.application.ui.component.CustomButton
import com.application.ui.component.CustomDatePicker
import com.application.ui.component.CustomSnackBarHost
import com.application.ui.component.CustomTextField
import com.application.ui.component.FieldToList
import com.application.ui.component.TopBar
import com.application.ui.viewmodel.CreateStageViewModel
import com.sc.library.utility.validate.RegexValidation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateStageScreen(
    viewModel: CreateStageViewModel = hiltViewModel(),
    projectId: String,
    navigateToLogin: () -> Unit,
    navigateToHome: () -> Unit,
    postCreatedHandler: (Boolean) -> Unit,
    navigateToWorkersQuestionScreen: () -> Unit,
    navigateToExpertChatScreen: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val snackBarHostState = remember { SnackbarHostState() }
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
        UiStatus.INIT -> viewModel.initialize(projectId)
        UiStatus.LOADING -> LoadingScreen(text = stringResource(id = R.string.creating_stage))
        UiStatus.SUCCESS -> {
            Scaffold(
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
                topBar = {
                    TopBar(
                        title = R.string.create_stage,
                        signOutClicked = navigateToLogin
                    )
                },
                bottomBar = {
                    BotNavigationBar(
                        modifier = Modifier.padding(vertical = 10.dp),
                        onWorkersQuestionClick = navigateToWorkersQuestionScreen,
                        onExpertChatsClick = navigateToExpertChatScreen
                    ) {
                        IconButton(
                            modifier = Modifier.size(50.dp),

                            onClick = navigateToHome
                        ) {
                            Icon(
                                modifier = Modifier.fillMaxSize(.6f),
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
                    CustomTextField(
                        modifier = Modifier.fillMaxWidth(.95f),
                        placeholder = { Text(text = stringResource(id = R.string.add_title)) },
                        singleLine = true,
                        value = state.name,
                        onValueChange = viewModel::updateName
                    )

                    CustomTextField(
                        modifier = Modifier
                            .fillMaxWidth(.95f)
                            .height(120.dp),
                        placeholder = { Text(text = "Add description") },
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

                    Card(
                        modifier = Modifier
                            .height(60.dp)
                            .fillMaxWidth(.95f),
                        shape = RoundedCornerShape(10.dp),
                    ) {
                        var expanded by remember { mutableStateOf(false) }
                        // We want to react on tap/press on TextField to show menu
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = it },
                        ) {
                            TextField(
                                modifier = Modifier
                                    .menuAnchor(MenuAnchorType.PrimaryEditable)
                                    .fillMaxSize(),
                                readOnly = true,
                                value = state.selectedForm?.second ?: "",
                                onValueChange = {},
                                label = { Text(text = stringResource(id = R.string.form)) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                colors = ExposedDropdownMenuDefaults.textFieldColors(
                                    unfocusedIndicatorColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedContainerColor = colorResource(id = R.color.gray_100),
                                    focusedContainerColor = colorResource(id = R.color.gray_100)
                                ),
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                state.forms.forEachIndexed { idx, form ->
                                    DropdownMenuItem(
                                        text = { Text(form.title) },
                                        onClick = {
                                            expanded = false
                                            viewModel.selectForm(idx)
                                        },
                                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                    )
                                }
                            }
                        }
                    }

                    FieldToList(
                        fieldDataList = state.stageMemberEmailMap.keys.toList(),
                        textValidator = { email ->
                            email.contains(RegexValidation.EMAIL)
                        },
                        listHeight = 180.dp,
                        onAddField = { newEmailMember ->
                            viewModel.addStageMemberEmail(
                                newEmailMember
                            )
                        },
                        onRemoveField = { index -> viewModel.removeMemberEmail(index) }
                    )

                    CustomButton(
                        modifier = Modifier.fillMaxWidth(.95f),
                        text = stringResource(id = R.string.submit),
                        textSize = 20.sp,
                        background = colorResource(id = R.color.main_green),
                        border = BorderStroke(0.dp, Color.Transparent),
                        action = {
                            state.selectedForm?.let {
                                viewModel.submitStage(
                                    projectId = projectId,
                                    formId = it.first,
                                    successHandler = postCreatedHandler
                                )
                            }
                        }
                    )
                }
            }
        }

        UiStatus.ERROR -> Toast.makeText(context, state.error!!, Toast.LENGTH_LONG).show()
    }
}