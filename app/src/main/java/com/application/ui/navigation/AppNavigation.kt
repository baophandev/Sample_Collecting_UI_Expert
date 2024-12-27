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
import com.application.ui.screen.ConversationsScreen
import com.application.ui.screen.HomeScreen
import com.application.ui.screen.LoginScreen
import com.application.ui.screen.ModifyFormScreen
import com.application.ui.screen.ModifyProjectScreen
import com.application.ui.screen.ModifyStageScreen
import com.application.ui.screen.PostDetailScreen
import com.application.ui.screen.SampleDetailScreen
import com.application.ui.screen.StageDetailScreen
import com.application.ui.screen.QuestionsScreen
import com.application.ui.viewmodel.CaptureViewModel
import com.application.ui.viewmodel.ChatViewModel
import com.application.ui.viewmodel.ConversationsViewModel
import com.application.ui.viewmodel.CreateFormViewModel
import com.application.ui.viewmodel.CreateSampleViewModel
import com.application.ui.viewmodel.CreateStageViewModel
import com.application.ui.viewmodel.DetailViewModel
import com.application.ui.viewmodel.HomeViewModel
import com.application.ui.viewmodel.LoginViewModel
import com.application.ui.viewmodel.ModifyFormViewModel
import com.application.ui.viewmodel.ModifyProjectViewModel
import com.application.ui.viewmodel.ModifyStageViewModel
import com.application.ui.viewmodel.PostDetailViewModel
import com.application.ui.viewmodel.QuestionsViewModel
import com.application.ui.viewmodel.SampleDetailViewModel
import com.application.ui.viewmodel.StageDetailViewModel

fun NavHostController.navigateSingleTop(route: String) {
    this.navigate(route) { launchSingleTop = true }
}

@Composable
fun AppNavigationGraph() {
    val loginVM: LoginViewModel = hiltViewModel()
    val homeVM: HomeViewModel = hiltViewModel()
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
    val postDetailVM: PostDetailViewModel = hiltViewModel()
    val questionsVM: QuestionsViewModel = hiltViewModel()
    val conversationsVM: ConversationsViewModel = hiltViewModel()
    val chatVM: ChatViewModel = hiltViewModel()

    val navController = rememberNavController()

    val navigateToConversations: () -> Unit = {
        navController.navigateSingleTop(Routes.CONVERSATIONS_SCREEN)
    }

    val navigateToQuestions: () -> Unit = {
        navController.navigateSingleTop(Routes.QUESTIONS_SCREEN)
    }

    val popBackToLogin: () -> Unit = {
        loginVM.logout()

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
            LoginScreen(viewModel = loginVM) {
                homeVM.reload(ReloadSignal.RELOAD_ALL_PROJECTS)
                questionsVM.reload(ReloadSignal.RELOAD_ALL_POSTS)
                conversationsVM.reload(ReloadSignal.RELOAD_ALL_CONVERSATIONS)

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
                viewModel = homeVM,
                navigateToLogin = popBackToLogin,
                navigateToCreateProject = navigateToCreateProject,
                navigateToDetailProject = navigateToDetailProject,
                navigateToQuestions = navigateToQuestions,
                navigateToConversations = navigateToConversations
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
                    if (isDeleted) homeVM.reload(ReloadSignal.RELOAD_ALL_PROJECTS)
                    popBackToHome()
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
                    if (isCreated) homeVM.reload(ReloadSignal.RELOAD_ALL_PROJECTS)
                    popBackToHome()
                },
                navigateToQuestions = navigateToQuestions,
                navigateToConversations = navigateToConversations
            )
        }

        composable(Routes.MODIFY_PROJECT_SCREEN) {
            ModifyProjectScreen(
                viewModel = modifyProjectVM,
                navigateToLogin = popBackToLogin,
                navigateToHome = popBackToHome,
                navigateToDetail = { isModified ->
                    if (isModified) {
                        detailVM.reload(ReloadSignal.RELOAD_PROJECT)
                        homeVM.reload(ReloadSignal.RELOAD_ALL_PROJECTS)
                    }
                    popBackToDetailScreen()
                },
                navigateToQuestions = navigateToQuestions,
                navigateToConversations = navigateToConversations
            )
        }

        composable(Routes.STAGE_DETAIL_SCREEN) {
            val navigateToModifyStage: (String, String) -> Unit = { projectId, stageId ->
                modifyStageVM.fetchStage(projectId, stageId)
                modifyStageVM.fetchProjectMembers(projectId)
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
                navigateToHome = popBackToHome,
                navigateToQuestions = navigateToQuestions,
                navigateToConversations = navigateToConversations
            )
        }

        composable(Routes.MODIFY_STAGE_SCREEN) {
            ModifyStageScreen(
                viewModel = modifyStageVM,
                navigateToLogin = popBackToLogin,
                navigateToHome = popBackToHome,
                navigateToStageDetail = { isUpdated ->
                    if (isUpdated) {
                        stageDetailVM.reload(ReloadSignal.RELOAD_STAGE)
                        detailVM.reload(ReloadSignal.RELOAD_STAGE)
                    }
                    popBackToStageDetailScreen()
                },
                navigateToQuestions = navigateToQuestions,
                navigateToConversations = navigateToConversations
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
                navigateToHome = popBackToHome,
                navigateToQuestions = navigateToQuestions,
                navigateToConversations = navigateToConversations
            )
        }

        composable(Routes.MODIFY_FORM_SCREEN) {
            ModifyFormScreen(
                viewModel = modifyFormVM,
                popBackToLogin = popBackToLogin,
                popBackToHome = popBackToHome,
                popBackToDetail = { isUpdated ->
                    if (isUpdated) detailVM.reload(ReloadSignal.RELOAD_FORM)
                    popBackToDetailScreen()
                },
                navigateToQuestions = navigateToQuestions,
                navigateToConversations = navigateToConversations
            )
        }

        composable(Routes.SAMPLE_DETAIL_SCREEN) {
            SampleDetailScreen(
                viewModel = sampleDetailVM,
                navigateToStageDetail = popBackToStageDetailScreen
            )
        }

        composable(Routes.CAPTURE_SCREEN) {
            val navigateToCreateSample: (String, Uri) -> Unit = { stageId, uri ->
                createSampleVM.loadFormFromStage(stageId)
                createSampleVM.loadSampleImage(uri)
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
            val navigateToCapture: (Uri?) -> Unit = {
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

        composable(Routes.QUESTIONS_SCREEN) {
            val navigateToPostDetail: (String) -> Unit = { postId ->
                postDetailVM.fetchPost(postId)
                navController.navigateSingleTop(Routes.POST_DETAIL_SCREEN)
            }
            QuestionsScreen(
                viewModel = questionsVM,
                navigateToHome = popBackToHome,
                navigateToConversations = navigateToConversations,
                navigateToPostDetail = navigateToPostDetail
            )
        }

        composable(Routes.POST_DETAIL_SCREEN) {
            val popBackToQuestions: () -> Unit = {
                navController.popBackStack(
                    route = Routes.QUESTIONS_SCREEN,
                    inclusive = false,
                    saveState = false
                )
            }

            PostDetailScreen(
                viewModel = postDetailVM,
                navigateToQuestions = popBackToQuestions,
                navigateToHome = popBackToHome,
                navigateToConversations = navigateToConversations,
            )
        }

        composable(Routes.CONVERSATIONS_SCREEN) {
            val navigateToChat: (Long) -> Unit = { conversationId ->
                chatVM.fetchMessages(conversationId)
                navController.navigateSingleTop(Routes.CHAT_SCREEN)
            }
            ConversationsScreen(
                viewModel = conversationsVM,
                navigateToHome = popBackToHome,
                navigateToChat = navigateToChat
            )
        }

        composable(Routes.CHAT_SCREEN) {
            val popBackToConversations: () -> Unit = {
                navController.popBackStack(
                    route = Routes.CONVERSATIONS_SCREEN,
                    inclusive = false,
                    saveState = false
                )
            }
            ChatScreen(
                viewModel = chatVM,
                navigateToConversations = popBackToConversations
            )
        }

    }
}


