package com.application.ui.screen

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.application.R
import com.application.ui.component.BotNavigationBar
import com.application.ui.component.CustomButton
import com.application.ui.component.FieldProject
import com.application.ui.component.PagingLayout
import com.application.ui.component.TitleText
import com.application.ui.component.TopBar
import com.application.ui.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    navigateToCreateProject: () -> Unit,
    navigateToDetailProject: (String) -> Unit,
    navigateToWorkersQuestionScreen: () -> Unit,
    navigateToExpertChatScreen: () -> Unit,
    signOutClick: () -> Unit
) {
    val projectPagingItems = viewModel.flow.collectAsLazyPagingItems()

    val context = LocalContext.current
    val signOut = stringResource(id = R.string.signed_out)
    var showLogoutDialog by remember { mutableStateOf(false) }

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
                    signOutClick()
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
                TopBar(title = R.string.home_screen_title, signOutClicked = { showLogoutDialog = true })
            }
        },
        bottomBar = {
            BotNavigationBar(
                onWorkersQuestionClick = navigateToWorkersQuestionScreen,
                onExpertChatsClick = navigateToExpertChatScreen
            ) {
                IconButton(
                    modifier = Modifier.size(50.dp),
                    onClick = navigateToCreateProject
                ) {
                    Icon(
                        modifier = Modifier.fillMaxSize(.75f),
                        painter = painterResource(id = R.drawable.ic_add_project),
                        contentDescription = "Add Project",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    ) { padding ->
        PagingLayout(
            modifier = Modifier.padding(padding),
            pagingItems = projectPagingItems,
            itemKey = projectPagingItems.itemKey { it.id },
            itemsContent = { project ->
                val ownerName = "${project.owner.firstName} ${project.owner.lastName}"

                FieldProject(
                    modifier = Modifier.clickable {
                        navigateToDetailProject(project.id)
                    },
                    thumbnail = project.thumbnail,
                    name = project.name,
                    description = project.description,
                    owner = ownerName
                )
                Spacer(modifier = Modifier.size(15.dp))
            },
            noItemContent = {
                Icon(
                    painter = painterResource(id = R.drawable.empty_icon),
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
        )
    }
}