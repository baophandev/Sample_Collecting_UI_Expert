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
    val detailVM: DetailViewModel = hiltViewModel()
    val modifyProjectVM: ModifyProjectViewModel = hiltViewModel()
    val stageDetailVM: StageDetailViewModel = hiltViewModel()
    val createStageVM: CreateStageViewModel = hiltViewModel()
    val modifyStageVM: ModifyStageViewModel = hiltViewModel()
    val createFormVM: CreateFormViewModel = hiltViewModel()
    val modifyFormVM: ModifyFormViewModel = hiltViewModel()
    val sampleDetailVM: SampleDetailViewModel = hiltViewModel()
    val captureVM: CaptureViewModel = hiltViewModel()
    val createSampleVM: CreateSampleViewModel = hiltViewModel()

    val navController = rememberNavController()

    val navigateToExpertChatsScreen: () -> Unit = {
        navController.navigateSingleTop(Routes.EXPERT_CHATS_SCREEN)
    }

    val navigateToWorkersQuestionScreen: () -> Unit = {
        navController.navigateSingleTop(Routes.WORKERS_QUESTION_SCREEN)
    }

    val popBackToLogin: () -> Unit = {
        detailVM.renewState()
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
                detailVM.fetchProject(projectId = projectId)
                detailVM.fetchForms(projectId = projectId)
                detailVM.fetchStages(projectId = projectId)
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

        composable(route = Routes.DETAIL_SCREEN) {
            val navigateToModify: (String) -> Unit = { projectIdToModify ->
                modifyProjectVM.loadProject(projectIdToModify)
                navController.navigateSingleTop(Routes.MODIFY_PROJECT_SCREEN)
            }
            val navigateToStageDetail: (String) -> Unit = { stageId ->
                stageDetailVM.loadStage(stageId)
                navController.navigateSingleTop(Routes.STAGE_DETAIL_SCREEN)
            }
            val navigateToCreateStage: (String) -> Unit = { projectId ->
                createStageVM.fetchForms(projectId = projectId)
                createStageVM.fetchProjectMembers(projectId = projectId)
                navController.navigateSingleTop(Routes.CREATE_STAGE_SCREEN)
            }
            val navigateToCreateForm: (String) -> Unit = { projectId ->
                createFormVM.fetchProject(projectId = projectId)
                navController.navigateSingleTop(Routes.CREATE_FORM_SCREEN)
            }
            val navigateToModifyForm: (String) -> Unit = { formId ->
                modifyFormVM.loadModifiedForm(formId)
                modifyFormVM.loadAllModifiedFields(formId)
                navController.navigateSingleTop(Routes.MODIFY_FORM_SCREEN)
            }

            DetailScreen(
                viewModel = detailVM,
                navigateToHome = { isDeleted ->
                    if (isDeleted) homeScreenVM.reload(ReloadSignal.RELOAD_ALL_PROJECTS)
                    popBackToHomeScreen()
                },
                navigateToModifyProject = navigateToModify,
                navigateToStageDetail = navigateToStageDetail,
                navigateToCreateStage = navigateToCreateStage,
                navigateToCreateForm = navigateToCreateForm,
                navigateToModifyForm = navigateToModifyForm,
            )
        }

        composable(Routes.CREATE_PROJECT_SCREEN) {
            CreateProjectScreen(
                navigateToLogin = popBackToLogin,
                navigateToHome = { isCreated ->
                    if (isCreated) homeScreenVM.reload(ReloadSignal.RELOAD_ALL_PROJECTS)
                    popBackToHomeScreen()
                },
                navigateToWorkersQuestionScreen = navigateToWorkersQuestionScreen,
                navigateToExpertChatScreen = navigateToExpertChatsScreen
            )
        }

        composable(Routes.MODIFY_PROJECT_SCREEN) {
            ModifyProjectScreen(
                viewModel = modifyProjectVM,
                navigateToLogin = popBackToLogin,
                navigateToHome = popBackToHomeScreen,
                navigateToDetail = { isModified ->
                    if (isModified) detailVM.reload(ReloadSignal.RELOAD_PROJECT)
                    popBackToDetailScreen()
                },
                navigateToWorkersQuestionScreen = navigateToWorkersQuestionScreen,
                navigateToExpertChatScreen = navigateToExpertChatsScreen
            )
        }

        composable(Routes.STAGE_DETAIL_SCREEN) {
            val navigateToModifyStage: (String, String) -> Unit = { projectId, stageId ->
                modifyStageVM.loadStage(projectId, stageId)
                navController.navigateSingleTop(Routes.MODIFY_STAGE_SCREEN)
            }
            val navigateToCapture: (String) -> Unit = { stageId ->
                captureVM.loadStage(stageId)
                navController.navigateSingleTop(Routes.CAPTURE_SCREEN)
            }
            val navigateToSampleDetail: (String) -> Unit = { sampleId ->
                sampleDetailVM.loadSample(sampleId)
                navController.navigateSingleTop(Routes.SAMPLE_DETAIL_SCREEN)
            }

            StageDetailScreen(
                viewModel = stageDetailVM,
                navigateToDetail = { isUpdated ->
                    if (isUpdated) detailVM.reload(ReloadSignal.RELOAD_STAGE)
                    popBackToDetailScreen()
                },
                navigateToModifyStage = navigateToModifyStage,
                navigateToCapture = navigateToCapture,
                navigateToSampleDetail = navigateToSampleDetail
            )
        }

        composable(Routes.CREATE_STAGE_SCREEN) {
            CreateStageScreen(
                viewModel = createStageVM,
                navigateToDetail = { isCreated ->
                    if (isCreated) detailVM.reload(ReloadSignal.RELOAD_STAGE)
                    popBackToDetailScreen()
                },
                navigateToLogin = popBackToLogin,
                navigateToHome = popBackToHomeScreen,
                navigateToWorkersQuestionScreen = navigateToWorkersQuestionScreen,
                navigateToExpertChatScreen = navigateToExpertChatsScreen
            )
        }

        composable(Routes.MODIFY_STAGE_SCREEN) {
            ModifyStageScreen(
                viewModel = modifyStageVM,
                navigateToLogin = popBackToLogin,
                navigateToHome = popBackToHomeScreen,
                navigateToStageDetail = { isUpdated ->
                    if (isUpdated) {
                        stageDetailVM.reload(ReloadSignal.RELOAD_STAGE)
                        detailVM.reload(ReloadSignal.RELOAD_STAGE)
                    }
                    popBackToStageDetailScreen()
                },
                navigateToWorkersQuestionScreen = navigateToWorkersQuestionScreen,
                navigateToExpertChatScreen = navigateToExpertChatsScreen
            )
        }

        composable(Routes.CREATE_FORM_SCREEN) {
            CreateFormScreen(
                viewModel = createFormVM,
                navigateToDetail = { isCreated ->
                    if (isCreated) detailVM.reload(ReloadSignal.RELOAD_FORM)
                    popBackToDetailScreen()
                },
                navigateToLogin = popBackToLogin,
                navigateToHome = popBackToHomeScreen,
                navigateToWorkersQuestionScreen = navigateToWorkersQuestionScreen,
                navigateToExpertChatScreen = navigateToExpertChatsScreen
            )
        }

        composable(Routes.MODIFY_FORM_SCREEN) {
            ModifyFormScreen(
                viewModel = modifyFormVM,
                popBackToLogin = popBackToLogin,
                popBackToHome = popBackToHomeScreen,
                popBackToDetail = { isUpdated ->
                    if (isUpdated) detailVM.reload(ReloadSignal.RELOAD_FORM)
                    popBackToDetailScreen()
                },
                navigateToWorkersQuestionScreen = navigateToWorkersQuestionScreen,
                navigateToExpertChatScreen = navigateToExpertChatsScreen
            )
        }

        composable(Routes.SAMPLE_DETAIL_SCREEN) {
            SampleDetailScreen(
                viewModel = sampleDetailVM,
                navigateToStageDetail = popBackToStageDetailScreen
            )
        }

        composable(Routes.CAPTURE_SCREEN) {
            val navigateToCreateSample: (String, Pair<String, Uri>) -> Unit = { stageId, sample ->
                createSampleVM.loadFormFromStage(stageId)
                createSampleVM.loadSampleImage(sample)
                navController.navigateSingleTop(Routes.CREATE_SAMPLE_SCREEN)
            }

            CaptureScreen(
                viewModel = captureVM,
                popBackToStage = {
                    stageDetailVM.reload(ReloadSignal.RELOAD_ALL_SAMPLES)
                    popBackToStageDetailScreen()
                },
                navigateToCreateSample = navigateToCreateSample
            )
        }

        composable(Routes.CREATE_SAMPLE_SCREEN) {
            val navigateToCapture: (String?) -> Unit = {
                it?.let(captureVM::removeCreatedSampleImage)
                navController.popBackStack(
                    route = Routes.CAPTURE_SCREEN,
                    inclusive = false,
                    saveState = false
                )
            }

            CreateSampleScreen(
                viewModel = createSampleVM,
                navigateToCapture = navigateToCapture,
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


