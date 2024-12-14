package com.application.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.application.R
import com.application.constant.UiStatus
import com.application.data.entity.Form
import com.application.data.entity.Stage
import com.application.ui.component.CustomButton
import com.application.ui.component.FormContainer
import com.application.ui.component.PagingLayout
import com.application.ui.component.StageContainer
import com.application.ui.component.TitleText
import com.application.ui.component.TopNavigationBar
import com.application.ui.viewmodel.DetailViewModel

private enum class ScreenTab { DETAIL, STAGES, FORMS }
private enum class AlertType { CREATE_NEW_PROJECT, DELETE, ADD_FORM, NONE, CANNOT_DELETE_FORM }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    viewModel: DetailViewModel = hiltViewModel(),
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

    var currentTab by remember { mutableStateOf(ScreenTab.DETAIL) }
    var alertType by remember { mutableStateOf(AlertType.NONE) }

    when (state.status) {
        UiStatus.INIT -> {
            viewModel.fetchProject(projectId = projectId)
            viewModel.fetchForms(projectId = projectId)
            viewModel.fetchStages(projectId = projectId)
        }

        UiStatus.LOADING -> LoadingScreen(text = stringResource(id = R.string.loading))
        UiStatus.ERROR -> navigateToHome() // Bị lỗi thì về Home
        UiStatus.SUCCESS -> {
            val stagePagingItems = viewModel.stageFlow.collectAsLazyPagingItems()
            val formPagingItems = viewModel.formFlow.collectAsLazyPagingItems()

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
                            TabButtons(tab = currentTab) { currentTab = it }
                            Column(modifier = Modifier.fillMaxSize()) {
                                when (currentTab) {
                                    ScreenTab.DETAIL -> DetailTab(
                                        projectDescription = state.project?.description
                                    )

                                    ScreenTab.STAGES -> StageTab(
                                        pagingItems = stagePagingItems,
                                        onStageClick = navigateToStageDetail
                                    )

                                    ScreenTab.FORMS -> FormTab(
                                        pagingItems = formPagingItems,
                                        onFormModifyClick = { formId ->
                                            if (viewModel.isProjectOwner())
                                                navigateToModifyForm(formId)
                                        },
                                        onFormDeleteClicked = { formId ->
                                            val isFormUsed = stagePagingItems.itemSnapshotList
                                                .any { it?.formId == formId }
                                            if (!isFormUsed) viewModel.deleteForm(formId = formId)
                                            else alertType = AlertType.CANNOT_DELETE_FORM
                                        }
                                    )
                                }
                            }
                        }
                    }
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        if (state.project?.thumbnail != null) {
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(state.project?.thumbnail)
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
                                painter = painterResource(id = R.drawable.ic_launcher_background),
                                contentDescription = "Default Thumbnail",
                                contentScale = ContentScale.Crop
                            )
                        }

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            TopNavigationBar(backAction = navigateToHome) {
                                if (viewModel.isProjectOwner()) {
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

                FunctionalButtons(
                    tab = currentTab,
                    onModifyProjectClick = {
                        if (viewModel.isProjectOwner())
                            navigateToModifyProject(state.project!!.id)
                        else alertType = AlertType.CREATE_NEW_PROJECT
                    },
                    onAddStageClick = {
                        if (viewModel.isProjectOwner()) {
                            if (formPagingItems.itemCount == 0)
                                alertType = AlertType.ADD_FORM
                            else navigateToAddStage()
                        } else alertType = AlertType.CREATE_NEW_PROJECT
                    },
                    onAddFormClick = {
                        if (viewModel.isProjectOwner()) navigateToAddForm()
                        else alertType = AlertType.CREATE_NEW_PROJECT
                    }
                )
            }
        }
    }
}

@Composable
private fun TabButtons(
    modifier: Modifier = Modifier,
    tab: ScreenTab,
    onTabChange: (ScreenTab) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        CustomButton(
            text = stringResource(id = R.string.details_button),
            textSize = 16.sp,
            textColor = if (tab == ScreenTab.DETAIL)
                Color.White else Color.Black,
            background = if (tab == ScreenTab.DETAIL)
                MaterialTheme.colorScheme.primary else Color.White,
            border = BorderStroke(
                2.dp, colorResource(id = R.color.main_green)
            ),
            action = { onTabChange(ScreenTab.DETAIL) }
        )
        CustomButton(
            text = stringResource(id = R.string.stages_button),
            textSize = 16.sp,
            textColor = if (tab == ScreenTab.STAGES)
                Color.White else Color.Black,
            background = if (tab == ScreenTab.STAGES)
                MaterialTheme.colorScheme.primary else Color.White,
            border = BorderStroke(
                2.dp,
                colorResource(id = R.color.main_green)
            ),
            action = { onTabChange(ScreenTab.STAGES) }
        )
        CustomButton(
            text = stringResource(id = R.string.forms_button),
            textSize = 16.sp,
            textColor = if (tab == ScreenTab.FORMS)
                Color.White else Color.Black,
            background = if (tab == ScreenTab.FORMS)
                MaterialTheme.colorScheme.primary else Color.White,
            border = BorderStroke(
                2.dp, colorResource(id = R.color.main_green)
            ),
            action = { onTabChange(ScreenTab.FORMS) }
        )
    }
}

@Composable
private fun FunctionalButtons(
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.BottomCenter,
    tab: ScreenTab,
    onModifyProjectClick: () -> Unit,
    onAddStageClick: () -> Unit,
    onAddFormClick: () -> Unit,
) {
    val btnWidthFraction = .7f
    val btnBackground = MaterialTheme.colorScheme.primary
    val btnTextSize = 16.sp
    val btnBorder = BorderStroke(0.dp, Color.Transparent)

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 20.dp),
        contentAlignment = contentAlignment
    ) {
        when (tab) {
            ScreenTab.DETAIL -> CustomButton(
                modifier = Modifier.fillMaxWidth(btnWidthFraction),
                text = stringResource(id = R.string.modify),
                textSize = btnTextSize,
                background = btnBackground,
                border = btnBorder,
                action = onModifyProjectClick
            )

            ScreenTab.STAGES -> CustomButton(
                modifier = Modifier.fillMaxWidth(btnWidthFraction),
                text = stringResource(id = R.string.add_stage),
                textSize = btnTextSize,
                background = btnBackground,
                border = btnBorder,
                action = onAddStageClick

            )

            ScreenTab.FORMS -> CustomButton(
                modifier = Modifier.fillMaxWidth(btnWidthFraction),
                text = stringResource(id = R.string.add_form),
                textSize = btnTextSize,
                background = btnBackground,
                border = btnBorder,
                action = onAddFormClick
            )
        }
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
    pagingItems: LazyPagingItems<Stage>,
    onStageClick: (String) -> Unit,
) {
    PagingLayout(
        modifier = modifier,
        pagingItems = pagingItems,
        contentAlignment = Alignment.TopCenter,
        itemKey = pagingItems.itemKey { it.id },
        itemsContent = { stage ->
            StageContainer(
                title = stage.name,
                description = stage.description,
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .clickable { onStageClick(stage.id) }
            )
        },
        noItemContent = {
            Icon(
                painter = painterResource(id = R.drawable.empty_icon),
                contentDescription = "No project",
                modifier = Modifier.size(100.dp),
                tint = colorResource(id = R.color.main_green)
            )
            TitleText(
                text = stringResource(id = R.string.no_stages),
                textSize = 20.sp,
                color = colorResource(id = R.color.main_green)
            )
        }
    )
}


@Composable
private fun FormTab(
    modifier: Modifier = Modifier,
    pagingItems: LazyPagingItems<Form>,
    onFormModifyClick: ((String) -> Unit)?,
    onFormDeleteClicked: ((String) -> Unit)?
) {
    PagingLayout(
        modifier = modifier,
        pagingItems = pagingItems,
        itemKey = pagingItems.itemKey { it.id },
        itemsContent = { form ->
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
        },
        noItemContent = {
            Icon(
                painter = painterResource(id = R.drawable.empty_icon),
                contentDescription = "No form",
                modifier = Modifier.size(100.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            TitleText(
                text = stringResource(id = R.string.no_forms),
                textSize = 20.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }
    )
}