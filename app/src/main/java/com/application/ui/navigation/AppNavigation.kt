package com.application.ui.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
import com.application.ui.screen.PostAnswerScreen
import com.application.ui.screen.SampleDetailScreen
import com.application.ui.screen.StageDetailScreen
import com.application.ui.screen.WorkersQuestionScreen
import com.application.ui.viewmodel.DetailViewModel

fun NavHostController.navigateSingleTop(route: String) {
    this.navigate(route) { launchSingleTop = true }
}

@Composable
fun AppNavigationGraph(
    viewModel: NavigationViewModel = hiltViewModel()
) {
    val detailScreenViewModel: DetailViewModel = hiltViewModel()

    val state by viewModel.state.collectAsState()
    val navController = rememberNavController()

    val navigateToExpertChatsScreen: () -> Unit = {
        navController.navigateSingleTop(Routes.EXPERT_CHATS_SCREEN)
    }

    val navigateToWorkersQuestionScreen: () -> Unit = {
        navController.navigateSingleTop(Routes.WORKERS_QUESTION_SCREEN)
    }

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

    val onReloadSuccessfully: (Boolean) -> Unit = {
        viewModel.updateState(state.copy(reloadSignal = ReloadSignal.NONE))
    }

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN_SCREEN
    ) {
        composable(Routes.LOGIN_SCREEN) {
            LoginScreen {
                navController.navigateSingleTop(Routes.HOME_SCREEN)
            }
        }

        composable(Routes.HOME_SCREEN) {
            val navigateToCreateProject: () -> Unit = {
                navController.navigateSingleTop(Routes.CREATE_PROJECT_SCREEN)
            }
            val navigateToDetailProject: (String) -> Unit = { projectId ->
                viewModel.updateState(state.copy(currentProjectId = projectId))
                navController.navigateSingleTop(Routes.DETAIL_SCREEN)
            }

            HomeScreen(
                signOutClick = {
                    detailScreenViewModel.renewState()
                    popBackToLogin()
                },
                navigateToCreateProject = navigateToCreateProject,
                navigateToDetailProject = navigateToDetailProject,
                navigateToWorkersQuestionScreen = navigateToWorkersQuestionScreen,
                navigateToExpertChatScreen = navigateToExpertChatsScreen
            )
        }

        composable(Routes.CREATE_PROJECT_SCREEN) {
            CreateProjectScreen(
                navigateToLogin = popBackToLogin,
                navigateToHome = {
                    // need to check reload
                    popBackToHome()
                },
                navigateToWorkersQuestionScreen = navigateToWorkersQuestionScreen,
                navigateToExpertChatScreen = navigateToExpertChatsScreen
            )
        }

        composable(route = Routes.DETAIL_SCREEN) {
            val projectId = state.currentProjectId

            if (projectId != null) {
                val navigateToModify: (String) -> Unit = { projectIdToModify ->
                    viewModel.updateState(state.copy(currentProjectId = projectIdToModify))
                    navController.navigateSingleTop(Routes.MODIFY_PROJECT_SCREEN)
                }
                val navigateToStageDetail: (String) -> Unit = { stageId ->
                    viewModel.updateState(state.copy(currentStageId = stageId))
                    navController.navigateSingleTop(Routes.STAGE_DETAIL_SCREEN)
                }
                val navigateToAddStage: () -> Unit = {
                    navController.navigateSingleTop(Routes.ADD_STAGE_SCREEN)
                }
                val navigateToAddForm: () -> Unit = {
                    navController.navigateSingleTop(Routes.ADD_FORM_SCREEN)
                }
                val navigateToModifyForm: (String) -> Unit = { formId ->
                    viewModel.updateState(state.copy(currentFormId = formId))
                    navController.navigateSingleTop(Routes.MODIFY_FORM_SCREEN)
                }

                DetailScreen(
                    viewModel = detailScreenViewModel,
                    projectId = projectId,
                    navigateToHome = popBackToHome,
                    navigateToModifyProject = navigateToModify,
                    navigateToStageDetail = navigateToStageDetail,
                    navigateToAddStage = navigateToAddStage,
                    navigateToAddForm = navigateToAddForm,
                    navigateToModifyForm = navigateToModifyForm,
                )
            }
        }

        composable(Routes.STAGE_DETAIL_SCREEN) {
            val stageId = state.currentStageId

            if (stageId != null) {
                val navigateToModifyStage: () -> Unit = {
                    navController.navigateSingleTop(Routes.MODIFY_STAGE_SCREEN)
                }
                val navigateToCapture: () -> Unit = {
                    navController.navigateSingleTop(Routes.CAPTURE_SCREEN)
                }
                val navigateToSampleDetail: (String) -> Unit = { sampleId ->
                    viewModel.updateState(state.copy(currentSampleId = sampleId))
                    navController.navigateSingleTop(Routes.SAMPLE_DETAIL_SCREEN)
                }

                StageDetailScreen(
                    reloadSignal = state.reloadSignal,
                    onReloadSuccessfully = onReloadSuccessfully,
                    deletedHandler = { isDeleted ->
                        if (isDeleted)
                            detailScreenViewModel.reload(ReloadSignal.RELOAD_STAGE)
                        popBackToDetail()
                    },
                    popBackToDetail = { isUpdated ->
                        if (isUpdated)
                            detailScreenViewModel.reload(ReloadSignal.RELOAD_STAGE)
                        popBackToDetail()
                    },
                    stageId = stageId,
                    navigateToModifyStage = navigateToModifyStage,
                    navigateToCapture = navigateToCapture,
                    navigateToSampleDetail = navigateToSampleDetail

                )
            }
        }

        composable(Routes.SAMPLE_DETAIL_SCREEN) {
            state.currentSampleId?.let { sampleId ->
                val popBackToStageDetail: () -> Unit = {
                    navController.popBackStack(
                        route = Routes.STAGE_DETAIL_SCREEN,
                        inclusive = false,
                        saveState = false
                    )
                }

                SampleDetailScreen(
                    sampleId = sampleId,
                    navigateToStageDetail = popBackToStageDetail
                )
            }
        }

        composable(Routes.ADD_FORM_SCREEN) {
            val projectId = state.currentProjectId

            if (projectId != null) {
                CreateFormScreen(
                    projectId = projectId,
                    postCreatedHandler = { isCreated ->
                        if (isCreated) detailScreenViewModel.reload(ReloadSignal.RELOAD_FORM)
                        popBackToDetail()
                    },
                    navigateToLogin = popBackToLogin,
                    navigateToHome = popBackToHome,
                    navigateToWorkersQuestionScreen = navigateToWorkersQuestionScreen,
                    navigateToExpertChatScreen = navigateToExpertChatsScreen
                )
            }
        }

        composable(Routes.ADD_STAGE_SCREEN) {
            val projectId = state.currentProjectId

            if (projectId != null) {
                CreateStageScreen(
                    projectId = projectId,
                    stageCreatedHandler = { isCreated ->
                        if (isCreated) detailScreenViewModel.reload(ReloadSignal.RELOAD_STAGE)
                        popBackToDetail()
                    },
                    navigateToLogin = popBackToLogin,
                    navigateToHome = popBackToHome,
                    navigateToWorkersQuestionScreen = navigateToWorkersQuestionScreen,
                    navigateToExpertChatScreen = navigateToExpertChatsScreen
                )
            }
        }

        composable(Routes.CAPTURE_SCREEN) {
            val navigateToCreateSample: (Pair<String, Uri>) -> Unit = { sample ->
                viewModel.updateState(state.copy(newSample = sample))
                navController.navigateSingleTop(Routes.CREATE_SAMPLE_SCREEN)
            }

            CaptureScreen(
                popBackToStage = popBackToStage,
                navigateToCreateSample = navigateToCreateSample
            )
        }

        composable(Routes.CREATE_SAMPLE_SCREEN) {
            val stageId = state.currentStageId
            val newSample = state.newSample

            if (stageId != null && newSample != null) {
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
                )
            }
        }

        composable(Routes.MODIFY_PROJECT_SCREEN) {
            val projectId = state.currentProjectId

            if (projectId != null) {
                ModifyProjectScreen(
                    projectId = projectId,
                    popBackToLogin = popBackToLogin,
                    popBackToHome = popBackToHome,
                    postUpdatedHandler = { isUpdated ->
                        if (isUpdated) detailScreenViewModel.reload(ReloadSignal.RELOAD_PROJECT)
                        popBackToDetail()
                    },
                    navigateToWorkersQuestionScreen = navigateToWorkersQuestionScreen,
                    navigateToExpertChatScreen = navigateToExpertChatsScreen
                )
            }
        }

        composable(Routes.MODIFY_STAGE_SCREEN) {
            val stageId = state.currentStageId
            val projectId = state.currentProjectId

            if (stageId != null && projectId != null) {
                ModifyStageScreen(
                    projectId = projectId,
                    stageId = stageId,
                    popBackToLogin = popBackToLogin,
                    popBackToHome = popBackToHome,
                    postUpdatedHandler = { isUpdated ->
                        if (isUpdated) detailScreenViewModel.reload(ReloadSignal.RELOAD_STAGE)
                        popBackToStage()
                    },
                    navigateToWorkersQuestionScreen = navigateToWorkersQuestionScreen,
                    navigateToExpertChatScreen = navigateToExpertChatsScreen
                )
            }
        }

        composable(Routes.MODIFY_FORM_SCREEN) {
            val formId = state.currentFormId

            if (formId != null) {
                ModifyFormScreen(
                    formId = formId,
                    popBackToLogin = popBackToLogin,
                    popBackToHome = popBackToHome,
                    popBackToDetail = { isUpdated ->
                        if (isUpdated) detailScreenViewModel.reload(ReloadSignal.RELOAD_FORM)
                        popBackToDetail()
                    },
                    navigateToWorkersQuestionScreen = navigateToWorkersQuestionScreen,
                    navigateToExpertChatScreen = navigateToExpertChatsScreen
                )
            }
        }

        composable(Routes.WORKERS_QUESTION_SCREEN) {
            val navigateToPostAnswerScreen: () -> Unit = {
                navController.navigateSingleTop(Routes.POST_ANSWER_SCREEN)
            }
            WorkersQuestionScreen(
                navigateToHome = { popBackToHome() },
                navigateToExpertChatScreen = navigateToExpertChatsScreen,
                navigateToPostAnswerScreen = navigateToPostAnswerScreen
            )
        }

        composable(Routes.EXPERT_CHATS_SCREEN) {
            val navigateToChatScreen: () -> Unit = {
                navController.navigateSingleTop(Routes.CHAT_SCREEN)
            }
            ExpertChatsScreen(
                navigateToHome = { popBackToHome() },
                navigateToChat = navigateToChatScreen
            )
        }

        composable(Routes.CHAT_SCREEN) {
            val navigateToExpertChatListScreen: () -> Unit = {
                navController.popBackStack()
            }
            ChatScreen(
                navigateToExpertChatListScreen = navigateToExpertChatListScreen
            )
        }

        composable(Routes.POST_ANSWER_SCREEN) {
            PostAnswerScreen(
                navigateToWorkersQuestionScreen = navigateToWorkersQuestionScreen
            )
        }

    }
}


