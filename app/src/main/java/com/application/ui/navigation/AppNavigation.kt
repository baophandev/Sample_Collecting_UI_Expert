package com.application.ui.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.application.R
import com.application.ui.screen.CaptureScreen
import com.application.ui.screen.CreateFormScreen
import com.application.ui.screen.CreateProjectScreen
import com.application.ui.screen.CreateStageScreen
import com.application.ui.screen.DetailScreen
import com.application.ui.screen.HomeScreen
import com.application.ui.screen.LoginScreen
import com.application.ui.screen.ModifyFormScreen
import com.application.ui.screen.ModifyProjectScreen
import com.application.ui.state.NavigationState

fun NavHostController.navigateSingleTop(route: String) {
    this.navigate(route) { launchSingleTop = true }
}

@Composable
fun AppNavigationGraph(
) {
    var state by remember { mutableStateOf(NavigationState()) }
    val navController = rememberNavController()
//    val data by viewModel.data.collectAsState()
    val context = LocalContext.current

    val projectNotExist = stringResource(id = R.string.project_not_exist)
    val stageNotExist = stringResource(id = R.string.stage_not_exist)

    val popBackToLogin: () -> Unit = {
        navController.popBackStack(
            Routes.LOGIN_SCREEN,
            inclusive = false,
            saveState = false
        )
    }
    val popBackToHome: () -> Unit = {
        navController.popBackStack(
            Routes.HOME_SCREEN,
            inclusive = false,
            saveState = false
        )
    }
    val popBackToDetail: () -> Unit = {
        navController.popBackStack(
            route = Routes.DETAIL_SCREEN,
            inclusive = false,
            saveState = false
        )
    }

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN_SCREEN
    ) {
        composable(Routes.LOGIN_SCREEN) { backStackEntry ->
            LoginScreen { userId ->
                backStackEntry.savedStateHandle[Routes.USER_ID_STACK_KEY] = userId
                navController.navigateSingleTop(Routes.HOME_SCREEN)
            }
        }

        composable(Routes.HOME_SCREEN) { backStackEntry ->
            val userId = navController
                .previousBackStackEntry?.savedStateHandle?.get<String>(Routes.USER_ID_STACK_KEY)
            userId?.let {
                backStackEntry.savedStateHandle[Routes.USER_ID_STACK_KEY] = userId

                val navigateToCreateProject: () -> Unit = {
                    navController.navigate(Routes.CREATE_PROJECT_SCREEN)
                }
                val navigateToDetailProject: (String) -> Unit = { projectId ->
                    backStackEntry.savedStateHandle[Routes.PROJECT_ID_STACK_KEY] = projectId
                    navController.navigate(Routes.DETAIL_SCREEN)
                }

                HomeScreen(
                    navigateToLogin = popBackToLogin,
                    navigateToCreateProject = navigateToCreateProject,
                    navigateToDetailProject = navigateToDetailProject,
                )
            }
        }

        composable(Routes.CREATE_PROJECT_SCREEN) {
            val userId = navController
                .previousBackStackEntry?.savedStateHandle?.get<String>(Routes.USER_ID_STACK_KEY)
            userId?.let {
                CreateProjectScreen(
                    userId = it,
                    navigateToLogin = popBackToLogin,
                    navigateToHome = {
                        popBackToHome()
                    }
                )
            }
        }

        composable(route = Routes.DETAIL_SCREEN) { backStackEntry ->
            val userId = navController
                .previousBackStackEntry?.savedStateHandle?.get<String>(Routes.USER_ID_STACK_KEY)
            val projectId = navController
                .previousBackStackEntry?.savedStateHandle?.get<String>(Routes.PROJECT_ID_STACK_KEY)

            if (projectId != null && userId != null) {
                backStackEntry.savedStateHandle[Routes.PROJECT_ID_STACK_KEY] = projectId

                val navigateToModify: () -> Unit = {
                    navController.navigate(Routes.MODIFY_PROJECT_SCREEN)
                }
                val navigateToStageDetail: (String) -> Unit = { stageId ->
                    backStackEntry.savedStateHandle[Routes.STAGE_ID_STACK_KEY] = stageId
                    navController.navigate(Routes.STAGE_DETAIL_SCREEN)
                }
                val navigateToAddStage: () -> Unit = {
                    navController.navigate(Routes.ADD_STAGE_SCREEN)
                }
                val navigateToAddForm: () -> Unit = {
                    navController.navigate(Routes.ADD_FORM_SCREEN)
                }
                val navigateToModifyForm: (String) -> Unit = { formId ->
                    backStackEntry.savedStateHandle[Routes.FORM_ID_STACK_KEY] = formId
                    navController.navigate(Routes.MODIFY_FORM_SCREEN)
                }

                DetailScreen(
                    projectId = projectId,
                    userId = userId,
                    needToReload = state.needToReload,
                    onReloadSuccessfully = { state = state.copy(needToReload = false) },
                    navigateToHome = popBackToHome,
                    navigateToModify = navigateToModify,
                    navigateToStageDetail = navigateToStageDetail,
                    navigateToAddStage = navigateToAddStage,
                    navigateToAddForm = navigateToAddForm,
                    navigateToModifyForm = navigateToModifyForm,
                )
            }
//            else {
//                LaunchedEffect(key1 = null) {
//                    Toast.makeText(context, projectNotExist, Toast.LENGTH_SHORT).show()
//                    navigateToHome(
//                }
//            }
        }

        composable(Routes.STAGE_DETAIL_SCREEN) {
            val stageId = navController
                .previousBackStackEntry?.savedStateHandle?.get<String>(Routes.STAGE_ID_STACK_KEY)

            if (stageId != null) {
                val navigateToModifyStage: (String) -> Unit =
                    { stageId -> navController.navigate(Routes.MODIFY_STAGE_SCREEN + "/$stageId") }
                val navigateToCapture: () -> Unit =
                    { navController.navigate(Routes.CAPTURE_SCREEN) }

//                StageDetailScreen(
//                    isProjectOwner = true,
//                    stageId = stageId,
//                    thumbnailUri = data.thumbnailUri,
//                    navigateToModifyStage = navigateToModifyStage,
//                    navigateToCapture = navigateToCapture,
//                    navigateToDetail = popBackToDetail
//                )
            }
//            else {
//                LaunchedEffect(key1 = null) {
//                    Toast.makeText(context, stageNotExist, Toast.LENGTH_SHORT).show()
//                    navigateToHome()
//                }
//            }
        }

        composable(Routes.ADD_FORM_SCREEN) {
            val projectId = navController
                .previousBackStackEntry?.savedStateHandle?.get<String>(Routes.PROJECT_ID_STACK_KEY)
            if (projectId != null) {
                CreateFormScreen(
                    projectId = projectId,
                    navigateToLogin = popBackToLogin,
                    navigateToHome = popBackToHome,
                    navigateToDetail = {
                        popBackToDetail()
                    }
                )
            }
//            else {
//                LaunchedEffect(key1 = null) {
//                    Toast.makeText(context, projectNotExist, Toast.LENGTH_SHORT).show()
//                    navigateToHome()
//                }
//            }
        }

        composable(Routes.ADD_STAGE_SCREEN) {
            val projectId = navController
                .previousBackStackEntry?.savedStateHandle?.get<String>(Routes.PROJECT_ID_STACK_KEY)
            if (projectId != null) {
                CreateStageScreen(
                    projectId = projectId,
                    navigateToLogin = popBackToLogin,
                    navigateToHome = popBackToHome,
                    navigateToDetail = popBackToDetail
                )
            }
//            else {
//                LaunchedEffect(key1 = null) {
//                    Toast.makeText(context, projectNotExist, Toast.LENGTH_SHORT).show()
//                    navigateToHome()
//                }
//            }
        }

        composable(Routes.CAPTURE_SCREEN) {
            val popBackToStage: () -> Unit = {
                navController.popBackStack(
                    route = Routes.STAGE_DETAIL_SCREEN,
                    inclusive = false,
                    saveState = false
                )
            }
            val navigateToCreateSample: (Pair<String, Uri>) -> Unit = { sample ->
//                viewModel.updateSample(sample)
                navController.navigate(Routes.CREATE_SAMPLE_SCREEN)
            }

            CaptureScreen(
                savedStateHandle = it.savedStateHandle,
                popBackToStage = popBackToStage,
                navigateToCreateSample = navigateToCreateSample
            )
        }

        composable(Routes.CREATE_SAMPLE_SCREEN) {
//            val project = data.project
//
//            if (project == null) {
//                LaunchedEffect(key1 = null) {
//                    Toast.makeText(context, projectNotExist, Toast.LENGTH_SHORT).show()
//                    navigateToHome()
//                }
//            } else if (data.stageId == null) {
//                LaunchedEffect(key1 = null) {
//                    Toast.makeText(context, stageNotExist, Toast.LENGTH_SHORT).show()
//                    navigateToDetail()
//                }
//            } else {
//                data.sample?.let { samplePair ->
//                    val formId = project.stages?.get(data.stageId)?.formId
//                    val formFields =
//                        project.forms?.get(formId)?.fields?.values?.toList()
//                    val navigateToCapture: (String?) -> Unit = { imageName ->
//                        navController.previousBackStackEntry?.savedStateHandle?.set(
//                            key = Routes.SAMPLE_STACK_KEY,
//                            value = imageName
//                        )
//                        navController.popBackStack(
//                            route = Routes.CAPTURE_SCREEN,
//                            inclusive = false,
//                            saveState = false
//                        )
//                    }
//
//                    CreateSampleScreen(
//                        isProjectOwner = data.isProjectOwner!!,
//                        projectId = project.id,
//                        stageId = data.stageId!!,
//                        sampleImage = samplePair,
//                        formFields = formFields,
//                        navigateToCapture = navigateToCapture,
//                        navigateToHome = navigateToHome
//                    )
//                }
//            }
        }

        composable(Routes.MODIFY_PROJECT_SCREEN) {
            val projectId = navController
                .previousBackStackEntry?.savedStateHandle?.get<String>(Routes.PROJECT_ID_STACK_KEY)
            if (projectId != null) {
                ModifyProjectScreen(
                    projectId = projectId,
                    popBackToLogin = popBackToLogin,
                    popBackToHome = popBackToHome,
                    postUpdatedHandler = { isUpdated ->
                        if (isUpdated)
                            state = state.copy(needToReload = true)
                        popBackToDetail()
                    }
                )
            }
//            else {
//                LaunchedEffect(key1 = null) {
//                    Toast.makeText(context, projectNotExist, Toast.LENGTH_SHORT).show()
//                    navigateToHome()
//                }
//            }
        }

        composable(Routes.MODIFY_STAGE_SCREEN + "/{stageId}") { stackEntry ->
//            val project = data.project
//            if (project != null) {
//                stackEntry.arguments?.getString("stageId")?.let { stageId ->
//                    project.data.stages?.get(stageId)?.let { stage ->
//                        ModifyStageScreen(
//                            projectId = project.id,
//                            projectEmailMembers = project.data.memberIds?.map { it.value },
//                            stage = Pair(stageId, stage),
//                            forms = project.data.forms!!.map { Pair(it.key, it.value.name!!) }
//                                .toMap(),
//                            navigateToLogin = navigateToLogin,
//                            navigateToHome = navigateToHome,
//                            navigateToStage = {
//                                navController.popBackStack(
//                                    route = Routes.STAGE_DETAIL_SCREEN,
//                                    inclusive = false,
//                                    saveState = false
//                                )
//                            }
//                        )
//                    }
//                }
//            } else {
//                LaunchedEffect(key1 = null) {
//                    Toast.makeText(context, projectNotExist, Toast.LENGTH_SHORT).show()
//                    navigateToHome()
//                }
//            }
        }

        composable(Routes.MODIFY_FORM_SCREEN) {
            val formId = navController
                .previousBackStackEntry?.savedStateHandle?.get<String>(Routes.FORM_ID_STACK_KEY)
            if (formId != null) {
                ModifyFormScreen(
                    formId = formId,
                    popBackToLogin = popBackToLogin,
                    popBackToHome = popBackToHome,
                    popBackToDetail = { isUpdated ->
                        if (isUpdated)
                            state = state.copy(needToReload = true)
                        popBackToDetail()
                    }
                )
//            val project = data.project
//            if (project != null) {
//                it.arguments?.getString("formId")?.let { formId ->
//                    project.data.forms?.get(formId)?.let { form ->
//                        ModifyFormScreen(
//                            projectId = project.id,
//                            form = Pair(formId, form),
//                            navigateToLogin = navigateToLogin,
//                            navigateToHome = navigateToHome,
//                            navigateToDetail = navigateToDetail
//                        )
//                    }
//                }
//            }
                //            else {
//                LaunchedEffect(key1 = null) {
//                    Toast.makeText(context, projectNotExist, Toast.LENGTH_SHORT).show()
//                    navigateToHome()
//                }
//            }
            }
        }
    }
}