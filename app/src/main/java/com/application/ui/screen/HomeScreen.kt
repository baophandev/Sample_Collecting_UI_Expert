package com.application.ui.screen

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.application.R
import com.application.data.entity.Project
import com.application.data.entity.User
import com.application.ui.component.BotNavigationBar
import com.application.ui.component.CustomButton
import com.application.ui.component.CustomCircularProgressIndicator
import com.application.ui.component.FieldProject
import com.application.ui.component.TitleText
import com.application.ui.component.TopBar
import com.application.ui.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    user: User,
    navigateToLogin: () -> Unit,
    navigateToCreateProject: () -> Unit,
    navigateToDetailProject: (Pair<Uri?, Project>) -> Unit
) {
    val state by viewModel.state.collectAsState()
    var isRefreshing by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val signOut = stringResource(id = R.string.signed_out)
    var showLogoutDialog by remember { mutableStateOf(false) }

    if (state.init) viewModel.getProjects(userEmail = user.email)
    else {
        if (showLogoutDialog) {
            AlertDialog(
                title = {
                    Text(text = stringResource(id = R.string.sign_out_title))
                },
                text = {
                    Text(text = stringResource(id = R.string.sign_out_description))
                },
                onDismissRequest = { showLogoutDialog = false },
                confirmButton = {
                    CustomButton(
                        text = stringResource(id = R.string.sign_out),
                        textSize = 14.sp,
                        background = colorResource(id = R.color.red),
                        border = BorderStroke(0.dp, Color.Transparent)
                    ) {
                        showLogoutDialog = false
                        Toast.makeText(context, signOut, Toast.LENGTH_SHORT).show()
                        navigateToLogin()
                    }
                },
                dismissButton = {
                    CustomButton(
                        text = stringResource(id = R.string.cancel),
                        textSize = 14.sp,
                        textColor = Color.Black,
                        background = Color.White,
                        border = BorderStroke(0.dp, Color.Transparent),
                        action = { showLogoutDialog = false }
                    )
                }
            )
        }

        BackHandler { showLogoutDialog = true }

        Scaffold(
            modifier = Modifier,
            topBar = {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    TopBar(title = R.string.home_screen_title, signOutClicked = navigateToLogin)
                }
            },
            bottomBar = {
                BotNavigationBar {
                    IconButton(
                        modifier = Modifier.size(60.dp),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = colorResource(id = R.color.smooth_blue)
                        ),
                        onClick = navigateToCreateProject
                    ) {
                        Icon(
                            modifier = Modifier.fillMaxSize(.60f),
                            painter = painterResource(id = R.drawable.ic_add_project),
                            contentDescription = "Add Project",
                            tint = Color.White
                        )
                    }
                }
            }
        ) { padding ->
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = {
                    isRefreshing = true

                    viewModel.getProjects(
                        userEmail = user.email,
                        refreshSuccess = { isRefreshing = false }
                    )
                }
            ) {
                if (state.projects.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                    ) {
                        items(state.projects) { project ->
                            FieldProject(
                                modifier = Modifier.clickable { navigateToDetailProject(project) },
                                thumbnailUri = project.first,
                                title = project.second.data.title,
                                description = project.second.data.description,
                                owner = project.second.data.emailOwner
                            )
                            Spacer(modifier = Modifier.size(15.dp))
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        contentAlignment = Alignment.Center
                    ) {
                        if (state.loading) {
                            CustomCircularProgressIndicator(
                                text = stringResource(id = R.string.load_projects)
                            )
                        } else if (state.error != null) {
                            TitleText(
                                text = stringResource(id = state.error ?: R.string.unknown_error),
                                textSize = 30.sp,
                                color = Color.Red
                            )
                        } else if (state.cancel != null) {
                            TitleText(
                                text = stringResource(id = state.cancel!!),
                                textSize = 30.sp,
                                color = Color.Red
                            )
                        } else if (state.projects.isEmpty()) {
//                            TitleText(
//                                text = stringResource(id = R.string.no_projects),
//                                textSize = 20.sp,
//                                color = Color.Black
//                            )
                            Column (
                                modifier = Modifier,
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ){
                                Icon(
                                    painter = painterResource(id = R.drawable.no_project_icon),
                                    contentDescription = "No project",
                                    modifier = Modifier.size(100.dp),
                                    tint = colorResource(id = R.color.main_green)
                                )
                                TitleText(
                                    text = stringResource(id = R.string.no_projects),
                                    textSize = 20.sp,
                                    color = colorResource(id = R.color.main_green)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}