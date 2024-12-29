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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.application.R
import com.application.constant.UiStatus
import com.application.ui.component.BotNavigationBar
import com.application.ui.component.CustomButton
import com.application.ui.component.CustomDatePicker
import com.application.ui.component.CustomSnackBarHost
import com.application.ui.component.CustomTextField
import com.application.ui.component.FieldToList
import com.application.ui.component.TopBar
import com.application.ui.viewmodel.ModifyStageViewModel
import io.github.nhatbangle.sc.utility.validate.RegexValidation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModifyStageScreen(
    viewModel: ModifyStageViewModel = hiltViewModel(),
    navigateToLogin: () -> Unit,
    navigateToHome: () -> Unit,
    navigateToStageDetail: (Boolean) -> Unit,
    navigateToQuestions: () -> Unit,
    navigateToConversations: () -> Unit
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }
    var expanded by remember { mutableStateOf(false) }

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
        UiStatus.LOADING -> LoadingScreen(text = stringResource(id = R.string.loading))
        UiStatus.SUCCESS -> {
            val formLazyPagingItems = viewModel.flow.collectAsLazyPagingItems()

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
                    TopBar(title = R.string.modify_stage, signOutClicked = navigateToLogin)
                },
                bottomBar = {
                    BotNavigationBar(
                        modifier = Modifier.padding(vertical = 10.dp),
                        onQuestionsClick = navigateToQuestions,
                        onExpertChatClick = navigateToConversations
                    ) {
                        IconButton(
                            modifier = Modifier.size(38.dp),

                            onClick = navigateToHome
                        ) {
                            Icon(
                                modifier = Modifier.fillMaxSize(.75f),
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
                        value = state.stage?.name ?: "",
                        onValueChange = viewModel::updateStageName
                    )

                    CustomTextField(
                        modifier = Modifier
                            .fillMaxWidth(.95f)
                            .height(120.dp),
                        placeholder = { Text(text = stringResource(id = R.string.add_description)) },
                        value = state.stage?.description ?: "",
                        onValueChange = viewModel::updateDescription
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(.95f),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        CustomDatePicker(
                            fieldName = stringResource(id = R.string.start_date),
                            initValue = state.stage?.startDate,
                            modifier = Modifier.width(160.dp)
                        ) { viewModel.updateDate(date = it, isStartDate = true) }
                        CustomDatePicker(
                            fieldName = stringResource(id = R.string.end_date),
                            initValue = state.stage?.endDate,
                            modifier = Modifier.width(160.dp)
                        ) { viewModel.updateDate(date = it, isStartDate = false) }
                    }

                    ExposedDropdownMenuBox(
                        modifier = Modifier.clip(RoundedCornerShape(15.dp)),
                        expanded = expanded,
                        onExpandedChange = {
//                            expanded = it
                        },
                    ) {
                        TextField(
                            modifier = Modifier
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                .fillMaxWidth(.95f),
                            readOnly = true,
                            value = state.selectedForm?.title ?: "Unknown form",
                            onValueChange = {},
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            colors = ExposedDropdownMenuDefaults.textFieldColors(
                                unfocusedIndicatorColor = colorResource(id = R.color.gray_100),
                                focusedIndicatorColor = colorResource(id = R.color.gray_100),
                                unfocusedContainerColor = colorResource(id = R.color.gray_100)
                            ),
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.fillMaxWidth(.95f),
                        ) {
                            if (formLazyPagingItems.itemCount == 0)
                                DropdownMenuItem(
                                    text = { Text("No form") },
                                    onClick = { expanded = false },
                                )
                            else
                                formLazyPagingItems.itemSnapshotList.forEach { form ->
                                    DropdownMenuItem(
                                        text = { Text(form?.title ?: "") },
                                        onClick = {
                                            expanded = false
                                            form?.let(viewModel::updateFormId)
                                        },
                                    )
                                }
                        }
                    }

                    FieldToList(
                        fieldDataList = state.stageUsers.map { it.email },
                        textValidator = { email -> email.contains(RegexValidation.EMAIL) },
                        listHeight = 180.dp,
                        onAddField = { newMemberEmail ->
                            viewModel.addNewStageMember(
                                newMemberEmail
                            )
                        },
                        onRemoveField = { index -> viewModel.removeMemberEmail(index) }
                    )

                    CustomButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 25.dp, vertical = 5.dp),
                        text = stringResource(id = R.string.save_button),
                        textSize = 16.sp,
                        background = colorResource(id = R.color.main_green),
                        border = BorderStroke(0.dp, Color.Transparent),
                        action = {
                            viewModel.submit(successHandler = navigateToStageDetail)
                        }
                    )
                }
            }
        }

        UiStatus.ERROR -> Toast.makeText(context, state.error!!, Toast.LENGTH_LONG).show()
        else -> {}
    }
}