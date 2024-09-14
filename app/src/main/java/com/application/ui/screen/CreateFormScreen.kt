package com.application.ui.screen

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.application.R
import com.application.ui.component.BotNavigationBar
import com.application.ui.component.CustomButton
import com.application.ui.component.CustomTextField
import com.application.ui.component.FormField
import com.application.ui.component.LoadingScreen
import com.application.ui.component.TopBar
import com.application.ui.viewmodel.FormViewModel

@Composable
fun CreateFormScreen(
    formViewModel: FormViewModel = hiltViewModel(),
    projectId: String,
    navigateToLogin: () -> Unit,
    navigateToHome: () -> Unit,
    navigateToDetail: () -> Unit,
) {
    val state by formViewModel.state.collectAsState()

    if (state.loading) LoadingScreen(text = stringResource(id = R.string.loading))
    else {
        Scaffold(
            modifier = Modifier.padding(horizontal = 15.dp, vertical = 5.dp),
            topBar = { TopBar(title = R.string.create_form, signOutClicked = navigateToLogin) },
            bottomBar = {
                BotNavigationBar {
                    IconButton(
                        modifier = Modifier.size(50.dp),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = colorResource(id = R.color.smooth_blue)
                        ),
                        onClick = navigateToHome
                    ) {
                        Icon(
                            modifier = Modifier.fillMaxSize(.60f),
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
                        .fillMaxWidth()
                        .height(80.dp)
                        .padding(vertical = 10.dp),
                    placeholder = { Text(text = stringResource(id = R.string.add_title)) },
                    singleLine = true,
                    value = state.title,
                    onValueChange = formViewModel::updateTitle
                )

                Row(
                    modifier = Modifier.fillMaxWidth()
                        .height(60.dp), // Adjust parent width as needed
                    horizontalArrangement = Arrangement.End  // Align elements to the right
                ) {
                    IconButton(
                        modifier = Modifier.size(40.dp),
                        onClick = { state.fields.add("") },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = colorResource(id = R.color.modBtn_color),
                        )
                    ) {
                        Icon(
                            modifier = Modifier.fillMaxSize(),
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add field",
                            tint = Color.White
                        )
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .padding(vertical = 20.dp)
                        .fillMaxWidth()
                        .fillMaxHeight(.9f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    itemsIndexed(state.fields) { index, data ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = colorResource(id = R.color.gray_color)
                            ),
                            shape = RoundedCornerShape(10.dp),
                        ) {
                            FormField(
                                fieldName = data,
                                onFieldNameChange = { state.fields[index] = it },
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
                            background = colorResource(id = R.color.smooth_blue),
                            border = BorderStroke(0.dp, Color.Transparent),
                            action = {
                                formViewModel.submitForm(
                                    projectId = projectId,
                                    successHandler = navigateToDetail
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}