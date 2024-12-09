package com.application.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.application.constant.ReloadSignal
import com.application.constant.UiStatus
import com.application.data.entity.Form
import com.application.data.entity.Stage
import com.application.ui.component.CustomButton
import com.application.ui.component.FormContainer
import com.application.ui.component.StageContainer
import com.application.ui.component.TopNavigationBar
import com.application.ui.viewmodel.DetailViewModel

private enum class DetailScreenSwitchState { DETAIL, STAGES, FORMS }
private enum class AlertType { CREATE_NEW_PROJECT, DELETE, ADD_FORM, NONE, CANNOT_DELETE_FORM }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    viewModel: DetailViewModel = hiltViewModel(),
    reloadSignal: ReloadSignal,
    onReloadSuccessfully: (Boolean) -> Unit,
    projectId: String,
    navigateToHome: () -> Unit,
    navigateToModifyProject: (String) -> Unit,
    navigateToStageDetail: (String) -> Unit,
    navigateToAddStage: () -> Unit,
    navigateToAddForm: () -> Unit,
    navigateToModifyForm: (String) -> Unit,
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

    var switch by remember { mutableStateOf(DetailScreenSwitchState.DETAIL) }
    var alertType by remember { mutableStateOf(AlertType.NONE) }

    val notInStage = stringResource(id = R.string.not_in_stage)

    if (reloadSignal != ReloadSignal.NONE) {
        when (reloadSignal) {
            ReloadSignal.RELOAD_STAGE ->
                viewModel.getStages(
                    projectId = projectId,
                    successHandler = onReloadSuccessfully
                )

            ReloadSignal.RELOAD_FORM ->
                viewModel.getForms(
                    projectId = projectId,
                    successHandler = onReloadSuccessfully
                )

            ReloadSignal.RELOAD_PROJECT ->
                viewModel.loadProject(
                    projectId = projectId,
                    skipCached = true,
                    onComplete = onReloadSuccessfully
                )

            else -> {}
        }
    }

    when (state.status) {
        UiStatus.INIT -> {
            viewModel.loadProject(projectId)
            viewModel.getForms(
                projectId = projectId, successHandler = null
            )
            viewModel.getStages(
                projectId = projectId, successHandler = null
            )
        }

        UiStatus.LOADING -> LoadingScreen(text = stringResource(id = R.string.loading))
        UiStatus.SUCCESS -> {
            if (alertType != AlertType.NONE) {
                val messages = when (alertType) {
                    AlertType.CREATE_NEW_PROJECT -> arrayOf(
                        R.string.modify_project,
                        R.string.modify_project_description,
                        R.string.create_project
                    )

                    AlertType.CANNOT_DELETE_FORM -> arrayOf(
                        R.string.cannot_delete_form,
                        R.string.cannot_delete_form_description,
                        R.string.cannot_delete_form_submit
                    )

                    AlertType.DELETE -> arrayOf(
                        R.string.delete_project,
                        R.string.delete_project_description,
                        R.string.delete_this_project
                    )

                    AlertType.ADD_FORM -> arrayOf(
                        R.string.add_form, R.string.not_exist_form, R.string.add_form
                    )

                    else -> emptyArray()
                }
                AlertDialog(title = {
                    Text(text = stringResource(id = messages[0]))
                }, text = {
                    Text(text = stringResource(id = messages[1]))
                }, onDismissRequest = { alertType = AlertType.NONE }, confirmButton = {
                    CustomButton(
                        text = stringResource(id = messages[2]),
                        textSize = 16.sp,
                        background = colorResource(
                            id = if (alertType == AlertType.DELETE) R.color.red
                            else R.color.main_green
                        ),
                        border = BorderStroke(0.dp, Color.Transparent)
                    ) {
                        when (alertType) {
                            AlertType.DELETE -> viewModel.deleteProject(
                                projectId = state.project!!.id,
                                projectOwnerId = state.project?.owner?.id,
                                successHandler = navigateToHome
                            )

                            AlertType.ADD_FORM -> navigateToAddForm()

                            else -> navigateToHome()
                        }

                        alertType = AlertType.NONE
                    }
                }, dismissButton = {
                    CustomButton(
                        text = stringResource(id = R.string.cancel),
                        textSize = 14.sp,
                        textColor = Color.Black,
                        background = Color.White,
                        border = BorderStroke(0.dp, Color.Transparent)
                    ) {
                        alertType = AlertType.NONE
                    }
                })
            }
            Box {
                BottomSheetScaffold(
                    scaffoldState = scaffoldState,
                    sheetPeekHeight = 600.dp,
                    sheetContent = {
                        Column(modifier = Modifier.fillMaxSize()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 5.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                CustomButton(
                                    text = stringResource(id = R.string.details_button),
                                    textSize = 16.sp,
                                    textColor = if (switch == DetailScreenSwitchState.DETAIL)
                                        Color.White else Color.Black,
                                    background = if (switch == DetailScreenSwitchState.DETAIL)
                                        MaterialTheme.colorScheme.primary else Color.White,
                                    border = BorderStroke(
                                        2.dp, colorResource(id = R.color.main_green)
                                    ),
                                    action = { switch = DetailScreenSwitchState.DETAIL }
                                )
                                CustomButton(
                                    text = stringResource(id = R.string.stages_button),
                                    textSize = 16.sp,
                                    textColor = if (switch == DetailScreenSwitchState.STAGES)
                                        Color.White else Color.Black,
                                    background = if (switch == DetailScreenSwitchState.STAGES)
                                        MaterialTheme.colorScheme.primary else Color.White,
                                    border = BorderStroke(
                                        2.dp,
                                        colorResource(id = R.color.main_green)
                                    ),
                                    action = { switch = DetailScreenSwitchState.STAGES }
                                )
                                CustomButton(
                                    text = stringResource(id = R.string.forms_button),
                                    textSize = 16.sp,
                                    textColor = if (switch == DetailScreenSwitchState.FORMS)
                                        Color.White else Color.Black,
                                    background = if (switch == DetailScreenSwitchState.FORMS)
                                        MaterialTheme.colorScheme.primary else Color.White,
                                    border = BorderStroke(
                                        2.dp, colorResource(id = R.color.main_green)
                                    ),
                                    action = { switch = DetailScreenSwitchState.FORMS }
                                )
                            }

                            Column(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                when (switch) {
                                    DetailScreenSwitchState.DETAIL ->
                                        DetailTab(projectDescription = state.project?.description)

                                    DetailScreenSwitchState.STAGES ->
                                        StageTab(
                                            status = state.stageStatus,
                                            error = state.stageError?.let { stringResource(id = it) },
                                            stages = state.stages,
//                                            onRefreshClick = {
//                                                viewModel.getStages(
//
//                                                    projectId = projectId,
//                                                    successHandler = null
//                                                )
//                                            },
                                            onStageClick = navigateToStageDetail
                                        )

                                    DetailScreenSwitchState.FORMS ->
                                        FormTab(
                                            status = state.formStatus,
                                            error = state.formError?.let { stringResource(id = it) },
                                            forms = state.forms,
//                                            onRefreshClick = {
//                                                viewModel.getForms(
//                                                    projectId = projectId,
//                                                    successHandler = null
//                                                )
//                                            },
                                            onFormModifyClick = { formId ->
                                                if (viewModel.isProjectOwner())
                                                    navigateToModifyForm(formId)
                                            },
                                            onFormDeleteClicked = { formId ->
                                                if (!viewModel.isFormUsed(formId)) {
                                                    viewModel.deleteForm(formId = formId)
                                                } else {
                                                    alertType = AlertType.CANNOT_DELETE_FORM
                                                }
                                            }
                                        )
                                }
                            }

                        }
                    }) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        if (state.project?.thumbnail != null) {
                            AsyncImage(
                                model = ImageRequest.Builder(context).data(state.project?.thumbnail)
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
                                contentScale = ContentScale.Crop
                            )
                        }

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            TopNavigationBar(
                                backAction = navigateToHome
                            ) {
                                val emailOwner = state.project?.owner?.id
                                if (emailOwner != null && emailOwner == state.project?.owner?.id) {
                                    DropdownMenuItem(
                                        leadingIcon = {
                                            Icon(
                                                modifier = Modifier.size(20.dp),
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Delete project",
                                                tint = colorResource(id = R.color.red)
                                            )
                                        },
                                        text = {
                                            Text(
                                                color = colorResource(id = R.color.red),
                                                text = stringResource(id = R.string.delete_project)
                                            )
                                        },
                                        onClick = { alertType = AlertType.DELETE }
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.size(40.dp))
                            state.project?.name?.let {
                                Text(
                                    text = it, fontSize = 30.sp, color = Color.White
                                )
                            }
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
                        DetailScreenSwitchState.DETAIL -> {
                            CustomButton(
                                modifier = Modifier.fillMaxWidth(.7f),
                                text = stringResource(id = R.string.modify),
                                textSize = 16.sp,
                                background = MaterialTheme.colorScheme.primary,
                                border = BorderStroke(0.dp, Color.Transparent)
                            ) {
                                if (viewModel.isProjectOwner())
                                    navigateToModifyProject(state.project!!.id)
                                else alertType = AlertType.CREATE_NEW_PROJECT
                            }
                        }

                        DetailScreenSwitchState.STAGES -> {
                            CustomButton(modifier = Modifier.fillMaxWidth(.7f),
                                text = stringResource(id = R.string.add_stage),
                                textSize = 16.sp,
                                background = MaterialTheme.colorScheme.primary,
                                border = BorderStroke(0.dp, Color.Transparent),
                                action = {
                                    if (viewModel.isProjectOwner()) {
                                        if (state.forms.isEmpty()) alertType = AlertType.ADD_FORM
                                        else navigateToAddStage()
                                    } else alertType = AlertType.CREATE_NEW_PROJECT
                                }

                            )
                        }

                        DetailScreenSwitchState.FORMS -> {
                            CustomButton(modifier = Modifier.fillMaxWidth(.7f),
                                text = stringResource(id = R.string.add_form),
                                textSize = 16.sp,
                                background = MaterialTheme.colorScheme.primary,
                                border = BorderStroke(0.dp, Color.Transparent),
                                action = {
                                    if (viewModel.isProjectOwner()) navigateToAddForm()
                                    else alertType = AlertType.CREATE_NEW_PROJECT
                                })
                        }
                    }
                }
            }
        }

        UiStatus.ERROR -> navigateToHome() // Bị lỗi thì về Home
    }
}

@Composable
private fun DetailTab(
    modifier: Modifier = Modifier, projectDescription: String? = null
) {
    Text(
        modifier = modifier.padding(horizontal = 30.dp, vertical = 15.dp),
        text = stringResource(id = R.string.detail),
        fontSize = 20.sp,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.W700
    )
    Text(
        modifier = Modifier.padding(horizontal = 30.dp),
        overflow = TextOverflow.Ellipsis,
        text = projectDescription ?: stringResource(id = R.string.default_project_description),
        fontSize = 16.sp,
        fontWeight = FontWeight.W400
    )
}

@Composable
private fun StageTab(
    modifier: Modifier = Modifier,
    status: UiStatus,
    error: String? = null,
    stages: List<Stage>,
    //onRefreshClick: () -> Unit,
    onStageClick: (String) -> Unit,
) {
    LazyColumn(
        modifier = modifier
            .padding(all = 10.dp)
            .fillMaxWidth(),
    ) {
//        stickyHeader {
//            Row(
//                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End
//            ) {
//                IconButton(onClick = onRefreshClick) {
//                    Icon(
//                        modifier = Modifier.fillMaxSize(.5f),
//                        painter = painterResource(id = R.drawable.return_arrow),
//                        contentDescription = "load new forms",
//                        tint = MaterialTheme.colorScheme.primary
//                    )
//                }
//            }
//        }
        when (status) {
            UiStatus.LOADING -> item {
                LoadingScreen(text = stringResource(id = R.string.loading))
            }

            UiStatus.SUCCESS -> items(items = stages, key = { it.id }) { stage ->
                StageContainer(
                    title = stage.name!!,
                    description = stage.description,
                    modifier = Modifier
                        .padding(vertical = 10.dp)
                        .clickable { onStageClick(stage.id) }
                )
            }

            UiStatus.ERROR -> item {
                Text(text = error ?: "Lỗi load stages rùi, sửa đi")
            }

            else -> {}
        }
    }
}


@Composable
private fun FormTab(
    modifier: Modifier = Modifier,
    status: UiStatus,
    error: String? = null,
    forms: List<Form>,
    //onRefreshClick: () -> Unit,
    onFormModifyClick: ((String) -> Unit)?,
    onFormDeleteClicked: ((String) -> Unit)?
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(10.dp)
    ) {
//        stickyHeader {
//            Row(
//                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End
//            ) {
//                IconButton(onClick = onRefreshClick) {
//                    Icon(
//                        modifier = Modifier.fillMaxSize(.5f),
//                        painter = painterResource(id = R.drawable.return_arrow),
//                        contentDescription = "load new forms",
//                        tint = MaterialTheme.colorScheme.primary
//                    )
//                }
//            }
//        }
        when (status) {
            UiStatus.LOADING -> item {
                LoadingScreen(text = stringResource(id = R.string.loading))
            }

            UiStatus.SUCCESS -> items(forms) { form ->
                Spacer(modifier = Modifier.size(10.dp))
                FormContainer(
                    name = form.title,
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .fillMaxWidth(),
                    onModifyClicked = {
                        if (onFormModifyClick != null)
                            onFormModifyClick(form.id)
                    },
                    onDeleteClicked = {
                        if (onFormDeleteClicked != null)
                            onFormDeleteClicked(form.id)
                    }
                )
            }

            UiStatus.ERROR -> item {
                Text(text = error ?: "Lỗi load stages rùi, sửa đi")
            }

            else -> {}
        }

    }

}