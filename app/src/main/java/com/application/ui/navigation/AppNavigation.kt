package com.application.ui.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
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
import com.application.ui.viewmodel.CaptureViewModel
import com.application.ui.viewmodel.CreateFormViewModel
import com.application.ui.viewmodel.CreateSampleViewModel
import com.application.ui.viewmodel.CreateStageViewModel
import com.application.ui.viewmodel.DetailViewModel
import com.application.ui.viewmodel.HomeViewModel
import com.application.ui.viewmodel.ModifyFormViewModel
import com.application.ui.viewmodel.ModifyProjectViewModel
import com.application.ui.viewmodel.ModifyStageViewModel
import com.application.ui.viewmodel.SampleDetailViewModel
import com.application.ui.viewmodel.StageDetailViewModel

fun NavHostController.navigateSingleTop(route: String) {
    this.navigate(route) { launchSingleTop = true }
}

@Composable
fun AppNavigationGraph() {
    val homeScreenVM: HomeViewModel = hiltViewModel()
    val detailScreenVM: DetailViewModel = hiltViewModel()
    val modifyProjectScreenVM: ModifyProjectViewModel = hiltViewModel()
    val stageDetailScreenVM: StageDetailViewModel = hiltViewModel()
    val createStageScreenVM: CreateStageViewModel = hiltViewModel()
    val modifyStageScreenVM: ModifyStageViewModel = hiltViewModel()
    val createFormScreenVM: CreateFormViewModel = hiltViewModel()
    val modifyFormScreenVM: ModifyFormViewModel = hiltViewModel()
    val sampleDetailScreenVM: SampleDetailViewModel = hiltViewModel()
    val captureScreenVM: CaptureViewModel = hiltViewModel()
    val createSampleScreenVM: CreateSampleViewModel = hiltViewModel()

    val navController = rememberNavController()

    val navigateToExpertChatsScreen: () -> Unit = {
        navController.navigateSingleTop(Routes.EXPERT_CHATS_SCREEN)
    }

    val navigateToWorkersQuestionScreen: () -> Unit = {
        navController.navigateSingleTop(Routes.WORKERS_QUESTION_SCREEN)
    }

    val popBackToLogin: () -> Unit = {
        detailScreenVM.renewState()
        navController.popBackStack(
            Routes.LOGIN_SCREEN,
            inclusive = false,
            saveState = false
        )
    }
    val popBackToHomeScreen: () -> Unit = {
        navController.popBackStack(
            Routes.HOME_SCREEN,
            inclusive = false,
            saveState = false
        )
    }
    val popBackToDetailScreen: () -> Unit = {
        navController.popBackStack(
            route = Routes.DETAIL_SCREEN,
            inclusive = false,
            saveState = false
        )
    }
    val popBackToStageDetailScreen: () -> Unit = {
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
            LoginScreen {
                navController.navigateSingleTop(Routes.HOME_SCREEN)
            }
        }

        composable(Routes.HOME_SCREEN) {
            val navigateToCreateProject: () -> Unit = {
                navController.navigateSingleTop(Routes.CREATE_PROJECT_SCREEN)
            }
            val navigateToDetailProject: (String) -> Unit = { projectId ->
                detailScreenVM.fetchProject(projectId = projectId)
                detailScreenVM.fetchForms(projectId = projectId)
                detailScreenVM.fetchStages(projectId = projectId)
                navController.navigateSingleTop(Routes.DETAIL_SCREEN)
            }

            HomeScreen(
                viewModel = homeScreenVM,
                navigateToLogin = popBackToLogin,
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
                    homeScreenVM.reload(ReloadSignal.RELOAD_ALL_PROJECTS)
                    popBackToHomeScreen()
                },
                navigateToWorkersQuestionScreen = navigateToWorkersQuestionScreen,
                navigateToExpertChatScreen = navigateToExpertChatsScreen
            )
        }

        composable(route = Routes.DETAIL_SCREEN) {
            val navigateToModify: (String) -> Unit = { projectIdToModify ->
                modifyProjectScreenVM.loadProject(projectIdToModify)
                navController.navigateSingleTop(Routes.MODIFY_PROJECT_SCREEN)
            }
            val navigateToStageDetail: (String) -> Unit = { stageId ->
                stageDetailScreenVM.loadStage(stageId)
                navController.navigateSingleTop(Routes.STAGE_DETAIL_SCREEN)
            }
            val navigateToCreateStage: (String) -> Unit = { projectId ->
                createStageScreenVM.fetchForms(projectId = projectId)
                createStageScreenVM.fetchProjectMembers(projectId = projectId)
                navController.navigateSingleTop(Routes.CREATE_STAGE_SCREEN)
            }
            val navigateToCreateForm: (String) -> Unit = { projectId ->
                createFormScreenVM.fetchProject(projectId = projectId)
                navController.navigateSingleTop(Routes.CREATE_FORM_SCREEN)
            }
            val navigateToModifyForm: (String) -> Unit = { formId ->
                modifyFormScreenVM.loadModifiedForm(formId)
                modifyFormScreenVM.loadAllModifiedFields(formId)
                navController.navigateSingleTop(Routes.MODIFY_FORM_SCREEN)
            }

            DetailScreen(
                viewModel = detailScreenVM,
                navigateToHome = popBackToHomeScreen,
                navigateToModifyProject = navigateToModify,
                navigateToStageDetail = navigateToStageDetail,
                navigateToCreateStage = navigateToCreateStage,
                navigateToCreateForm = navigateToCreateForm,
                navigateToModifyForm = navigateToModifyForm,
            )
        }

        composable(Routes.STAGE_DETAIL_SCREEN) {
            val navigateToModifyStage: (String, String) -> Unit = { projectId, stageId ->
                modifyStageScreenVM.loadStage(projectId, stageId)
                navController.navigateSingleTop(Routes.MODIFY_STAGE_SCREEN)
            }
            val navigateToCapture: (String) -> Unit = { stageId ->
                captureScreenVM.loadStage(stageId)
                navController.navigateSingleTop(Routes.CAPTURE_SCREEN)
            }
            val navigateToSampleDetail: (String) -> Unit = { sampleId ->
                sampleDetailScreenVM.loadSample(sampleId)
                navController.navigateSingleTop(Routes.SAMPLE_DETAIL_SCREEN)
            }

            StageDetailScreen(
                viewModel = stageDetailScreenVM,
                popBackToDetail = { isUpdated ->
                    if (isUpdated)
                        detailScreenVM.reload(ReloadSignal.RELOAD_STAGE)
                    popBackToDetailScreen()
                },
                navigateToModifyStage = navigateToModifyStage,
                navigateToCapture = navigateToCapture,
                navigateToSampleDetail = navigateToSampleDetail
            )
        }

        composable(Routes.SAMPLE_DETAIL_SCREEN) {
            SampleDetailScreen(
                viewModel = sampleDetailScreenVM,
                navigateToStageDetail = popBackToStageDetailScreen
            )
        }

        composable(Routes.CREATE_FORM_SCREEN) {
            CreateFormScreen(
                viewModel = createFormScreenVM,
                navigateToDetail = { isCreated ->
                    if (isCreated) detailScreenVM.reload(ReloadSignal.RELOAD_FORM)
                    popBackToDetailScreen()
                },
                navigateToLogin = popBackToLogin,
                navigateToHome = popBackToHomeScreen,
                navigateToWorkersQuestionScreen = navigateToWorkersQuestionScreen,
                navigateToExpertChatScreen = navigateToExpertChatsScreen
            )
        }

        composable(Routes.CREATE_STAGE_SCREEN) {
            CreateStageScreen(
                viewModel = createStageScreenVM,
                stageCreatedHandler = { isCreated ->
                    if (isCreated) detailScreenVM.reload(ReloadSignal.RELOAD_STAGE)
                    popBackToDetailScreen()
                },
                navigateToLogin = popBackToLogin,
                navigateToHome = popBackToHomeScreen,
                navigateToWorkersQuestionScreen = navigateToWorkersQuestionScreen,
                navigateToExpertChatScreen = navigateToExpertChatsScreen
            )
        }

        composable(Routes.CAPTURE_SCREEN) {
            val navigateToCreateSample: (String, Pair<String, Uri>) -> Unit = { stageId, sample ->
                createSampleScreenVM.loadFormFromStage(stageId)
                createSampleScreenVM.loadSampleImage(sample)
                navController.navigateSingleTop(Routes.CREATE_SAMPLE_SCREEN)
            }

            CaptureScreen(
                viewModel = captureScreenVM,
                popBackToStage = {
                    stageDetailScreenVM.reload(ReloadSignal.RELOAD_ALL_SAMPLES)
                    popBackToStageDetailScreen()
                },
                navigateToCreateSample = navigateToCreateSample
            )
        }

        composable(Routes.CREATE_SAMPLE_SCREEN) {
            val navigateToCapture: (String?) -> Unit = {
                it?.let(captureScreenVM::removeCreatedSampleImage)
                navController.popBackStack(
                    route = Routes.CAPTURE_SCREEN,
                    inclusive = false,
                    saveState = false
                )
            }

            CreateSampleScreen(
                viewModel = createSampleScreenVM,
                navigateToCapture = navigateToCapture,
            )
        }

        composable(Routes.MODIFY_PROJECT_SCREEN) {
            ModifyProjectScreen(
                viewModel = modifyProjectScreenVM,
                popBackToLogin = popBackToLogin,
                popBackToHome = popBackToHomeScreen,
                postUpdatedHandler = { isUpdated ->
                    if (isUpdated) detailScreenVM.reload(ReloadSignal.RELOAD_PROJECT)
                    popBackToDetailScreen()
                },
                navigateToWorkersQuestionScreen = navigateToWorkersQuestionScreen,
                navigateToExpertChatScreen = navigateToExpertChatsScreen
            )
        }

        composable(Routes.MODIFY_STAGE_SCREEN) {
            ModifyStageScreen(
                viewModel = modifyStageScreenVM,
                popBackToLogin = popBackToLogin,
                popBackToHome = popBackToHomeScreen,
                postUpdatedHandler = { isUpdated ->
                    if (isUpdated) {
                        stageDetailScreenVM.reload(ReloadSignal.RELOAD_STAGE)
                        detailScreenVM.reload(ReloadSignal.RELOAD_STAGE)
                    }
                    popBackToStageDetailScreen()
                },
                navigateToWorkersQuestionScreen = navigateToWorkersQuestionScreen,
                navigateToExpertChatScreen = navigateToExpertChatsScreen
            )
        }

        composable(Routes.MODIFY_FORM_SCREEN) {
            ModifyFormScreen(
                viewModel = modifyFormScreenVM,
                popBackToLogin = popBackToLogin,
                popBackToHome = popBackToHomeScreen,
                popBackToDetail = { isUpdated ->
                    if (isUpdated) detailScreenVM.reload(ReloadSignal.RELOAD_FORM)
                    popBackToDetailScreen()
                },
                navigateToWorkersQuestionScreen = navigateToWorkersQuestionScreen,
                navigateToExpertChatScreen = navigateToExpertChatsScreen
            )
        }

        composable(Routes.WORKERS_QUESTION_SCREEN) {
            val navigateToPostAnswerScreen: () -> Unit = {
                navController.navigateSingleTop(Routes.POST_ANSWER_SCREEN)
            }
            WorkersQuestionScreen(
                navigateToHome = { popBackToHomeScreen() },
                navigateToExpertChatScreen = navigateToExpertChatsScreen,
                navigateToPostAnswerScreen = navigateToPostAnswerScreen
            )
        }

        composable(Routes.EXPERT_CHATS_SCREEN) {
            val navigateToChatScreen: () -> Unit = {
                navController.navigateSingleTop(Routes.CHAT_SCREEN)
            }
            ExpertChatsScreen(
                navigateToHome = { popBackToHomeScreen() },
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


