package com.application.ui.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.application.R
import com.application.constant.ReloadSignal
import com.application.ui.screen.CaptureScreen
import com.application.ui.screen.ChatScreen
import com.application.ui.screen.CreateFormScreen
import com.application.ui.screen.CreateProjectScreen
import com.application.ui.screen.CreateSampleScreen
import com.application.ui.screen.CreateStageScreen
import com.application.ui.screen.DetailScreen
import com.application.ui.screen.ExpertChatsScreen
import com.application.ui.screen.HomeScreen
import com.application.ui.screen.LoginScreen
import com.application.ui.screen.ModifyFormScreen
import com.application.ui.screen.ModifyProjectScreen
import com.application.ui.screen.ModifyStageScreen
import com.application.ui.screen.StageDetailScreen
import com.application.ui.screen.WorkersQuestionScreen
import com.application.ui.state.NavigationState

fun NavHostController.navigateSingleTop(route: String) {
    this.navigate(route) { launchSingleTop = true }
}

@Composable
fun AppNavigationGraph() {
    var state by remember { mutableStateOf(NavigationState()) }
    val navController = rememberNavController()

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
    val popBackToStage: () -> Unit = {
        navController.popBackStack(
            route = Routes.STAGE_DETAIL_SCREEN,
            inclusive = false,
            saveState = false
        )
    }

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN_SCREEN
    ) {
        composable(Routes.LOGIN_SCREEN) {
            LoginScreen { userId ->
//                backStackEntry.savedStateHandle[Routes.USER_ID_STACK_KEY] = userId
                state = state.copy(loggedInUserId = userId)
                navController.navigateSingleTop(Routes.HOME_SCREEN)
            }
        }

        composable(Routes.HOME_SCREEN) { backStackEntry ->
//            val userId = navController
//                .previousBackStackEntry?.savedStateHandle?.get<String>(Routes.USER_ID_STACK_KEY)
//            userId?.let {
//                backStackEntry.savedStateHandle[Routes.USER_ID_STACK_KEY] = userId
//
//                val navigateToCreateProject: () -> Unit = {
//                    navController.navigateSingleTop(Routes.CREATE_PROJECT_SCREEN)
//                }
//                val navigateToDetailProject: (String) -> Unit = { projectId ->
//                    backStackEntry.savedStateHandle[Routes.PROJECT_ID_STACK_KEY] = projectId
//                    navController.navigateSingleTop(Routes.DETAIL_SCREEN)
//                }
//                val navigateToWorkersQuestionScreen: () -> Unit = {
//                    navController.navigateSingleTop(Routes.WORKERS_QUESTION_SCREEN)
//                }
//                val navigateToExpertChatsScreen: () -> Unit = {
//                    navController.navigateSingleTop(Routes.EXPERT_CHATS_SCREEN)
//                }
//
//                HomeScreen(
//                    navigateToLogin = popBackToLogin,
//                    navigateToCreateProject = navigateToCreateProject,
//                    navigateToDetailProject = navigateToDetailProject,
//                    navigateToWorkersQuestionScreen = navigateToWorkersQuestionScreen,
//                    navigateToExpertChatScreen = navigateToExpertChatsScreen
//                )
//            }
            val navigateToCreateProject: () -> Unit = {
                navController.navigateSingleTop(Routes.CREATE_PROJECT_SCREEN)
            }
            val navigateToDetailProject: (String) -> Unit = { projectId ->
//                backStackEntry.savedStateHandle[Routes.PROJECT_ID_STACK_KEY] = projectId
                state = state.copy(currentProjectId = projectId)
                navController.navigateSingleTop(Routes.DETAIL_SCREEN)
            }
            val navigateToWorkersQuestionScreen: () -> Unit = {
                navController.navigateSingleTop(Routes.WORKERS_QUESTION_SCREEN)
            }
            val navigateToExpertChatsScreen: () -> Unit = {
                navController.navigateSingleTop(Routes.EXPERT_CHATS_SCREEN)
            }

            HomeScreen(
                navigateToLogin = popBackToLogin,
                navigateToCreateProject = navigateToCreateProject,
                navigateToDetailProject = navigateToDetailProject,
                navigateToWorkersQuestionScreen = navigateToWorkersQuestionScreen,
                navigateToExpertChatScreen = navigateToExpertChatsScreen
            )
        }

        composable(Routes.CREATE_PROJECT_SCREEN) {
            val navigateToWorkersQuestionScreen: () -> Unit = {
                navController.navigateSingleTop(Routes.WORKERS_QUESTION_SCREEN)
            }
            val navigateToExpertChatsScreen: () -> Unit = {
                navController.navigateSingleTop(Routes.EXPERT_CHATS_SCREEN)
            }
//            val userId = navController
//                .previousBackStackEntry?.savedStateHandle?.get<String>(Routes.USER_ID_STACK_KEY)
//            userId?.let {
//                CreateProjectScreen(
//                    userId = it,
//                    navigateToLogin = popBackToLogin,
//                    navigateToHome = {
//                        popBackToHome()
//                    },
//                    navigateToWorkersQuestionScreen = navigateToWorkersQuestionScreen,
//                    navigateToExpertChatScreen = navigateToExpertChatsScreen
//                )
//            }
            state.loggedInUserId?.let {
                CreateProjectScreen(
                    userId = it,
                    navigateToLogin = popBackToLogin,
                    navigateToHome = {
                        // need to check reload
                        popBackToHome()
                    },
                    navigateToWorkersQuestionScreen = navigateToWorkersQuestionScreen,
                    navigateToExpertChatScreen = navigateToExpertChatsScreen
                )
            }
        }

        composable(route = Routes.DETAIL_SCREEN) { backStackEntry ->
//            val userId = navController
//                .previousBackStackEntry?.savedStateHandle?.get<String>(Routes.USER_ID_STACK_KEY)
//            val projectId = navController
//                .previousBackStackEntry?.savedStateHandle?.get<String>(Routes.PROJECT_ID_STACK_KEY)
            val userId = state.loggedInUserId
            val projectId = state.currentProjectId

            if (projectId != null && userId != null) {
                backStackEntry.savedStateHandle[Routes.PROJECT_ID_STACK_KEY] = projectId

                val navigateToModify: () -> Unit = {
                    navController.navigateSingleTop(Routes.MODIFY_PROJECT_SCREEN)
                }
                val navigateToStageDetail: (String) -> Unit = { stageId ->
//                    backStackEntry.savedStateHandle[Routes.STAGE_ID_STACK_KEY] = stageId
//                    backStackEntry.savedStateHandle[Routes.PROJECT_ID_STACK_KEY] = projectId
                    state = state.copy(currentStageId = stageId)
                    navController.navigateSingleTop(Routes.STAGE_DETAIL_SCREEN)
                }
                val navigateToAddStage: () -> Unit = {
                    navController.navigateSingleTop(Routes.ADD_STAGE_SCREEN)
                }
                val navigateToAddForm: () -> Unit = {
                    navController.navigateSingleTop(Routes.ADD_FORM_SCREEN)
                }
                val navigateToModifyForm: (String) -> Unit = { formId ->
//                    backStackEntry.savedStateHandle[Routes.FORM_ID_STACK_KEY] = formId
                    state = state.copy(currentFormId = formId)
                    navController.navigateSingleTop(Routes.MODIFY_FORM_SCREEN)
                }

                DetailScreen(
                    projectId = projectId,
                    userId = userId,
                    reloadSignal = state.reloadSignal,
                    onReloadSuccessfully = { state = state.copy(reloadSignal = ReloadSignal.NONE) },
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
//            val stageId = navController
//                .previousBackStackEntry?.savedStateHandle?.get<String>(Routes.STAGE_ID_STACK_KEY)
//            val projectId = navController
//                .previousBackStackEntry?.savedStateHandle?.get<String>(Routes.PROJECT_ID_STACK_KEY)
            val stageId = state.currentStageId

            if (stageId != null) {
                val navigateToModifyStage: () -> Unit = {
//                    backStackEntry.savedStateHandle[Routes.STAGE_ID_STACK_KEY] = stageId
//                    backStackEntry.savedStateHandle[Routes.PROJECT_ID_STACK_KEY] = projectId
                    navController.navigateSingleTop(Routes.MODIFY_STAGE_SCREEN)
                }
                val navigateToCapture: () -> Unit = {
                    navController.navigateSingleTop(Routes.CAPTURE_SCREEN)
                }

                StageDetailScreen(
                    stageId = stageId,
                    //thumbnailUri = data.thumbnailUri,
                    navigateToModifyStage = navigateToModifyStage,
                    navigateToCapture = navigateToCapture,
                    navigateToDetail = popBackToDetail
                )
            }
//            else {
//                LaunchedEffect(key1 = null) {
//                    Toast.makeText(context, stageNotExist, Toast.LENGTH_SHORT).show()
//                    navigateToHome()
//                }
//            }
        }

        composable(Routes.ADD_FORM_SCREEN) {
//            val projectId = navController
//                .previousBackStackEntry?.savedStateHandle?.get<String>(Routes.PROJECT_ID_STACK_KEY)
            val projectId = state.currentProjectId

            if (projectId != null) {
                val navigateToWorkersQuestionScreen: () -> Unit = {
                    navController.navigateSingleTop(Routes.WORKERS_QUESTION_SCREEN)
                }
                val navigateToExpertChatsScreen: () -> Unit = {
                    navController.navigateSingleTop(Routes.EXPERT_CHATS_SCREEN)
                }

                CreateFormScreen(
                    projectId = projectId,
                    navigateToLogin = popBackToLogin,
                    navigateToHome = popBackToHome,
                    navigateToDetail = {
                        popBackToDetail()
                    },
                    navigateToWorkersQuestionScreen = navigateToWorkersQuestionScreen,
                    navigateToExpertChatScreen = navigateToExpertChatsScreen
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
//            val projectId = navController
//                .previousBackStackEntry?.savedStateHandle?.get<String>(Routes.PROJECT_ID_STACK_KEY)
            val projectId = state.currentProjectId

            if (projectId != null) {
                val navigateToWorkersQuestionScreen: () -> Unit = {
                    navController.navigateSingleTop(Routes.WORKERS_QUESTION_SCREEN)
                }
                val navigateToExpertChatsScreen: () -> Unit = {
                    navController.navigateSingleTop(Routes.EXPERT_CHATS_SCREEN)
                }

                CreateStageScreen(
                    projectId = projectId,
                    navigateToLogin = popBackToLogin,
                    navigateToHome = popBackToHome,
                    navigateToDetail = popBackToDetail,
                    navigateToWorkersQuestionScreen = navigateToWorkersQuestionScreen,
                    navigateToExpertChatScreen = navigateToExpertChatsScreen
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
            val navigateToCreateSample: (Pair<String, Uri>) -> Unit = { sample ->
                state = state.copy(newSample = sample)
                navController.navigateSingleTop(Routes.CREATE_SAMPLE_SCREEN)
            }

            CaptureScreen(
                savedStateHandle = it.savedStateHandle,
                popBackToStage = popBackToStage,
                navigateToCreateSample = navigateToCreateSample
            )
        }

        composable(Routes.CREATE_SAMPLE_SCREEN) {
            val projectId = state.currentProjectId
            val stageId = state.currentStageId
            val newSample = state.newSample

            if (projectId != null && stageId != null && newSample != null) {
                val navigateToCapture: (String?) -> Unit = { imageName ->
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        key = Routes.SAMPLE_STACK_KEY,
                        value = imageName
                    )
                    navController.popBackStack(
                        route = Routes.CAPTURE_SCREEN,
                        inclusive = false,
                        saveState = false
                    )
                }


                CreateSampleScreen(
                    stageId = stageId,
                    newSample = newSample,
                    navigateToCapture = navigateToCapture,
                    navigateToHome = popBackToHome
                )
            }
        }

        composable(Routes.MODIFY_PROJECT_SCREEN) {
            val projectId = navController
                .previousBackStackEntry?.savedStateHandle?.get<String>(Routes.PROJECT_ID_STACK_KEY)
            val navigateToWorkersQuestionScreen: () -> Unit = {
                navController.navigateSingleTop(Routes.WORKERS_QUESTION_SCREEN)
            }
            val navigateToExpertChatsScreen: () -> Unit = {
                navController.navigateSingleTop(Routes.EXPERT_CHATS_SCREEN)
            }
            if (projectId != null) {
                ModifyProjectScreen(
                    projectId = projectId,
                    popBackToLogin = popBackToLogin,
                    popBackToHome = popBackToHome,
                    postUpdatedHandler = { isUpdated ->
                        if (isUpdated)
                            state = state.copy(reloadSignal = ReloadSignal.RELOAD_PROJECT)
                        popBackToDetail()
                    },
                    navigateToWorkersQuestionScreen = navigateToWorkersQuestionScreen,
                    navigateToExpertChatScreen = navigateToExpertChatsScreen
                )
            }
//            else {
//                LaunchedEffect(key1 = null) {
//                    Toast.makeText(context, projectNotExist, Toast.LENGTH_SHORT).show()
//                    navigateToHome()
//                }
//            }
        }

        composable(Routes.MODIFY_STAGE_SCREEN) {
//            val stageId = navController
//                .previousBackStackEntry?.savedStateHandle?.get<String>(Routes.STAGE_ID_STACK_KEY)
//            val projectId = navController
//                .previousBackStackEntry?.savedStateHandle?.get<String>(Routes.PROJECT_ID_STACK_KEY)

            val stageId = state.currentStageId
            val projectId = state.currentProjectId

            if (stageId != null && projectId != null) {
                val navigateToWorkersQuestionScreen: () -> Unit = {
                    navController.navigateSingleTop(Routes.WORKERS_QUESTION_SCREEN)
                }
                val navigateToExpertChatsScreen: () -> Unit = {
                    navController.navigateSingleTop(Routes.EXPERT_CHATS_SCREEN)
                }

                ModifyStageScreen(
                    projectId = projectId,
                    stageId = stageId,
                    popBackToLogin = popBackToLogin,
                    popBackToHome = popBackToHome,
                    postUpdatedHandler = { isUpdated ->
                        if (isUpdated)
                            state = state.copy(reloadSignal = ReloadSignal.RELOAD_STAGE)
                        popBackToStage()
                    },
                    navigateToWorkersQuestionScreen = navigateToWorkersQuestionScreen,
                    navigateToExpertChatScreen = navigateToExpertChatsScreen
                )
            }
        }
//            else {
//                LaunchedEffect(key1 = null) {
//                    Toast.makeText(context, projectNotExist, Toast.LENGTH_SHORT).show()
//                    navigateToHome()
//                }
//            }


        composable(Routes.MODIFY_FORM_SCREEN) {
//            val formId = navController
//                .previousBackStackEntry?.savedStateHandle?.get<String>(Routes.FORM_ID_STACK_KEY)
            val formId = state.currentFormId

            if (formId != null) {
                val navigateToWorkersQuestionScreen: () -> Unit = {
                    navController.navigateSingleTop(Routes.WORKERS_QUESTION_SCREEN)
                }
                val navigateToExpertChatsScreen: () -> Unit = {
                    navController.navigateSingleTop(Routes.EXPERT_CHATS_SCREEN)
                }

                ModifyFormScreen(
                    formId = formId,
                    popBackToLogin = popBackToLogin,
                    popBackToHome = popBackToHome,
                    popBackToDetail = { isUpdated ->
                        if (isUpdated)
                            state = state.copy(reloadSignal = ReloadSignal.RELOAD_FORM)
                        popBackToDetail()
                    },
                    navigateToWorkersQuestionScreen = navigateToWorkersQuestionScreen,
                    navigateToExpertChatScreen = navigateToExpertChatsScreen
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

        composable(Routes.WORKERS_QUESTION_SCREEN) {
            val userId = navController
                .previousBackStackEntry?.savedStateHandle?.get<String>(Routes.USER_ID_STACK_KEY)
            val navigateToWorkersQuestionScreen: () -> Unit = {
                navController.navigateSingleTop(Routes.WORKERS_QUESTION_SCREEN)
            }
            val navigateToExpertChatsScreen: () -> Unit = {
                navController.navigateSingleTop(Routes.EXPERT_CHATS_SCREEN)
            }
            userId?.let {
                WorkersQuestionScreen(
                    navigateToHome = {
                        popBackToHome()
                    },
                    navigateToWorkersQuestionScreen = navigateToWorkersQuestionScreen,
                    navigateToExpertChatScreen = navigateToExpertChatsScreen
                )
            }
        }

        composable(Routes.EXPERT_CHATS_SCREEN) {
            val userId = navController
                .previousBackStackEntry?.savedStateHandle?.get<String>(Routes.USER_ID_STACK_KEY)
            val navigateToChatScreen: () -> Unit = {
                navController.navigateSingleTop(Routes.CHAT_SCREEN)
            }
            userId?.let {
                ExpertChatsScreen(
                    navigateToHome = {
                        popBackToHome()
                    },
                    navigateToChat = navigateToChatScreen
                )
            }
        }

        composable(Routes.CHAT_SCREEN) {
            val navigateToExpertChatListScreen: () -> Unit = {
                navController.popBackStack()
            }
            ChatScreen(
                navigateToExpertChatListScreen = navigateToExpertChatListScreen
            )
        }
    }
}


