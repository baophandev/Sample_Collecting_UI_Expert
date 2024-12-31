package com.application.ui.screen

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.application.constant.UiStatus
import com.application.ui.component.BotNavigationBar
import com.application.ui.component.CustomButton
import com.application.ui.component.CustomSnackBarHost
import com.application.ui.component.CustomTextField
import com.application.ui.component.FormField
import com.application.ui.component.TopBar
import com.application.ui.viewmodel.CreateFormViewModel
import com.application.util.Validation

@Composable
fun CreateFormScreen(
    viewModel: CreateFormViewModel = hiltViewModel(),
    navigateToDetail: (Boolean) -> Unit,
    navigateToLogin: () -> Unit,
    navigateToHome: () -> Unit,
    navigateToQuestions: () -> Unit,
    navigateToConversations: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val snackBarHostState = remember { SnackbarHostState() }
    if (state.error != null) {
        val error = stringResource(id = state.error!!)
        LaunchedEffect(key1 = state.error) {
            val result = snackBarHostState.showSnackbar(
                message = error,
                withDismissAction = true,
                duration = SnackbarDuration.Short
            )
            if (result == SnackbarResult.Dismissed) {
                viewModel.gotError()
            }
        }
    }
    when (state.status) {
        UiStatus.LOADING -> LoadingScreen(text = stringResource(id = R.string.loading))
        UiStatus.SUCCESS -> {
            Scaffold(
                modifier = Modifier,
                snackbarHost = {
                    CustomSnackBarHost(
                        snackBarHostState = snackBarHostState,
                        dismissAction = {
                            IconButton(
                                modifier = Modifier
                                    .padding(end = 10.dp)
                                    .size(30.dp),
                                onClick = viewModel::gotError
                            ) {
                                Icon(
                                    modifier = Modifier.fillMaxSize(),
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Close"
                                )
                            }
                        }
                    )
                },
                topBar = { TopBar(title = R.string.create_form, signOutClicked = navigateToLogin) },
                bottomBar = {
                    BotNavigationBar(
                        onQuestionsClick = navigateToQuestions,
                        onExpertChatClick = navigateToConversations
                    ) {
                        IconButton(
                            modifier = Modifier.size(38.dp),
                            onClick = navigateToHome
                        ) {
                            Icon(
                                modifier = Modifier.fillMaxSize(.75f),
                                painter = painterResource(id = R.drawable.ic_home),
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    }
                }
            ) { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CustomTextField(
                        modifier = Modifier
                            .fillMaxWidth(.95f)
                            .height(80.dp),
                        placeholder = { Text(text = stringResource(id = R.string.add_title)) },
                        singleLine = true,
                        value = state.title,
                        supportingText = {
                            val text =
                                "${stringResource(R.string.str_max_length)} ${Validation.NORMAL_TEXT_LENGTH}"
                            Text(text = text)
                        },
                        onValueChange = {
                            if (Validation.checkNormalText(it))
                                viewModel.updateTitle(it)
                        }
                    )

                    Row(
                        modifier = Modifier
                            .padding(end = 5.dp)
                            .fillMaxWidth(.95f)
                            .height(50.dp), // Adjust parent width as needed
                        horizontalArrangement = Arrangement.End  // Align elements to the right
                    ) {
                        IconButton(
                            modifier = Modifier.size(40.dp),
                            onClick = { state.fields.add("") },

                            ) {
                            Icon(
                                modifier = Modifier.fillMaxSize(),
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add field",
                                tint = colorResource(id = R.color.main_green)
                            )
                        }
                    }

                    LazyColumn(
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth()
                            .fillMaxHeight(.9f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        itemsIndexed(state.fields) { index, data ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.secondary
                                ),
                                shape = RoundedCornerShape(10.dp),
                            ) {
                                FormField(
                                    fieldName = data,
                                    supportingText = {
                                        val text =
                                            "${stringResource(R.string.str_max_length)} ${Validation.NORMAL_TEXT_LENGTH}"
                                        Text(text = text)
                                    },
                                    onFieldNameChange = {
                                        if (Validation.checkNormalText(it))
                                            state.fields[index] = it
                                    },
                                    onDeleteClicked = { state.fields.removeAt(index) }
                                )
                            }
                            Spacer(modifier = Modifier.size(10.dp))
                        }
                    }

                    if (state.fields.size > 0) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = 10.dp),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            CustomButton(
                                modifier = Modifier
                                    .fillMaxWidth(.8f)
                                    .height(50.dp),
                                text = stringResource(id = R.string.save_button),
                                textSize = 16.sp,
                                background = colorResource(id = R.color.main_green),
                                border = BorderStroke(0.dp, Color.Transparent),
                                action = { viewModel.submit { navigateToDetail(true) } }
                            )
                        }
                    }
                }
            }
        }

        UiStatus.ERROR -> Toast.makeText(context, state.error!!, Toast.LENGTH_LONG).show()
        else -> {}
    }

}