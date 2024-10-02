package com.application.ui.navigation

import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.application.R
import com.application.data.entity.Project
import com.application.ui.screen.CaptureScreen
import com.application.ui.screen.CreateFormScreen
import com.application.ui.screen.CreateProjectScreen
import com.application.ui.screen.HomeScreen
import com.application.ui.screen.ModifyProjectScreen

@Composable
fun AppNavigationGraph(
    viewModel: NavigationViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val data by viewModel.data.collectAsState()
    val context = LocalContext.current

    val projectNotExist = stringResource(id = R.string.project_not_exist)
    val stageNotExist = stringResource(id = R.string.stage_not_exist)

    val navigateToLogin: () -> Unit = {
        navController.popBackStack(
            Routes.LOGIN_SCREEN,
            inclusive = false,
            saveState = false
        )
    }
    val navigateToHome: () -> Unit = {
        navController.popBackStack(
            Routes.HOME_SCREEN,
            inclusive = false,
            saveState = false
        )
    }
    val navigateToDetail: () -> Unit = {
        navController.popBackStack(
            route = Routes.DETAIL_SCREEN,
            inclusive = false,
            saveState = false
        )
    }

    NavHost(
        navController = navController,
        startDestination = Routes.HOME_SCREEN
    ) {
//        composable(Routes.LOGIN_SCREEN) {
//            LoginScreen { user ->
//                viewModel.updateUser(user)
//                navController.navigate(Routes.HOME_SCREEN)
//            }
//        }

        composable(Routes.HOME_SCREEN) {
            data.userId?.let {
                val navigateToCreateProject: () -> Unit =
                    { navController.navigate(Routes.CREATE_PROJECT_SCREEN) }
                val navigateToDetailProject: (Project) -> Unit = { data ->
                    viewModel.updateThumbnailUri(data.thumbnail)
                    viewModel.updateProject(data)
                    navController.navigate(Routes.DETAIL_SCREEN)
                }

                HomeScreen(
                    userId = it,
                    navigateToLogin = navigateToLogin,
                    navigateToCreateProject = navigateToCreateProject,
                    navigateToDetailProject = navigateToDetailProject,
                )
            }
        }

        composable(Routes.CREATE_PROJECT_SCREEN) {
            data.userId?.let {
                CreateProjectScreen(
                    userId = it,
                    navigateToLogin = navigateToLogin,
                    navigateToHome = { project ->
                        if (project != null) {
                            navigateToHome()
                        } else navigateToHome()
                    }
                )
            }
        }

        composable(Routes.DETAIL_SCREEN) {
//            val project = data.project
//            if (project != null) {
//                val navigateToModify: () -> Unit =
//                    { navController.navigate(Routes.MODIFY_PROJECT_SCREEN) }
//                val navigateToStage: (String) -> Unit = { stageId ->
//                    viewModel.updateStageId(stageId)
//                    navController.navigate(Routes.STAGE_DETAIL_SCREEN)
//                }
//                val navigateToAddStage: () -> Unit =
//                    { navController.navigate(Routes.ADD_STAGE_SCREEN) }
//                val navigateToAddForm: () -> Unit =
//                    { navController.navigate(Routes.ADD_FORM_SCREEN) }
//                val navigateToModifyForm: (String) -> Unit =
//                    { formId -> navController.navigate(Routes.MODIFY_FORM_SCREEN + "/$formId") }
//                val updateProjectData: (Project) -> Unit =
//                    { viewModel.updateProject(data.project?.copy(data = it)) }
//
//                DetailScreen(
//                    userEmail = data.user!!.username,
//                    project = project,
//                    thumbnailUri = data.thumbnailUri,
//                    navigateToHome = navigateToHome,
//                    navigateToModify = navigateToModify,
//                    navigateToStage = navigateToStage,
//                    navigateToAddStage = navigateToAddStage,
//                    navigateToAddForm = navigateToAddForm,
//                    navigateToModifyForm = navigateToModifyForm,
//                    updateProjectData = updateProjectData
//                )
//            } else {
//                LaunchedEffect(key1 = null) {
//                    Toast.makeText(context, projectNotExist, Toast.LENGTH_SHORT).show()
//                    navigateToHome()
//                }
//            }
        }

        composable(Routes.ADD_FORM_SCREEN) {
            val project = data.project
            if (project != null) {
                CreateFormScreen(
                    projectId = project.id,
                    navigateToLogin = navigateToLogin,
                    navigateToHome = navigateToHome,
                    navigateToDetail = navigateToDetail
                )
            } else {
                LaunchedEffect(key1 = null) {
                    Toast.makeText(context, projectNotExist, Toast.LENGTH_SHORT).show()
                    navigateToHome()
                }
            }
        }

        composable(Routes.ADD_STAGE_SCREEN) {
            val project = data.project
            if (project != null) {
//                project.data.forms?.let { forms ->
//                    CreateStageScreen(
//                        projectId = project.id,
//                        projectEmailMembers = project.data.memberIds?.map { it.value },
//                        forms = forms.map { Pair(it.key, it.value.name!!) }.toMap(),
//                        navigateToLogin = navigateToLogin,
//                        navigateToHome = navigateToHome,
//                        navigateToDetail = navigateToDetail
//                    )
//                }
            } else {
                LaunchedEffect(key1 = null) {
                    Toast.makeText(context, projectNotExist, Toast.LENGTH_SHORT).show()
                    navigateToHome()
                }
            }
        }

        composable(Routes.STAGE_DETAIL_SCREEN) {
//            val navigateToModifyStage: (String) -> Unit =
//                { stageId -> navController.navigate(Routes.MODIFY_STAGE_SCREEN + "/$stageId") }
//            val navigateToCapture: () -> Unit =
//                { navController.navigate(Routes.CAPTURE_SCREEN) }
//
//            val project = data.project
//            val currentStage = project?.data?.stages?.get(data.stageId)
//            if (currentStage != null) {
//                StageDetailScreen(
//                    isProjectOwner = data.isProjectOwner!!,
//                    projectId = project.id,
//                    thumbnailUri = data.thumbnailUri,
//                    stage = Pair(data.stageId!!, currentStage),
//                    navigateToModifyStage = navigateToModifyStage,
//                    navigateToCapture = navigateToCapture,
//                    navigateToDetail = navigateToDetail
//                )
//            } else if (project != null) {
//                LaunchedEffect(key1 = null) {
//                    Toast.makeText(context, stageNotExist, Toast.LENGTH_SHORT).show()
//                    navigateToDetail()
//                }
//            } else {
//                LaunchedEffect(key1 = null) {
//                    Toast.makeText(context, projectNotExist, Toast.LENGTH_SHORT).show()
//                    navigateToHome()
//                }
//            }
        }

        composable(Routes.CAPTURE_SCREEN) {
            val navigateToStage: () -> Unit = {
                navController.popBackStack(
                    route = Routes.STAGE_DETAIL_SCREEN,
                    inclusive = false,
                    saveState = false
                )
            }
            val navigateToCreateSample: (Pair<String, Uri>) -> Unit = { sample ->
                viewModel.updateSample(sample)
                navController.navigate(Routes.CREATE_SAMPLE_SCREEN)
            }

            CaptureScreen(
                savedStateHandle = it.savedStateHandle,
                navigateToStage = navigateToStage,
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
            val project = data.project
            if (project != null) {
                ModifyProjectScreen(
                    project = project,
                    thumbnailUri = data.thumbnailUri,
                    navigateToLogin = navigateToLogin,
                    navigateToHome = navigateToHome,
                    navigateToDetail = navigateToDetail
                )
            } else {
                LaunchedEffect(key1 = null) {
                    Toast.makeText(context, projectNotExist, Toast.LENGTH_SHORT).show()
                    navigateToHome()
                }
            }
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

        composable(Routes.MODIFY_FORM_SCREEN + "/{formId}") {
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
//            } else {
//                LaunchedEffect(key1 = null) {
//                    Toast.makeText(context, projectNotExist, Toast.LENGTH_SHORT).show()
//                    navigateToHome()
//                }
//            }
        }
    }
}