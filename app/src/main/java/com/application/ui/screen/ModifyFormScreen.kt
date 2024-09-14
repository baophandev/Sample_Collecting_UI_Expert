package com.application.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.application.R
import com.application.data.entity.Form
import com.application.ui.component.BotNavigationBar
import com.application.ui.component.CustomButton
import com.application.ui.component.CustomTextField
import com.application.ui.component.FormField
import com.application.ui.component.LoadingScreen
import com.application.ui.component.TopBar
import com.application.ui.viewmodel.ModifyFormViewModel

@Composable
fun ModifyFormScreen(
    viewModel: ModifyFormViewModel = hiltViewModel(),
    projectId: String,
    form: Pair<String, Form>,
    navigateToLogin: () -> Unit,
    navigateToHome: () -> Unit,
    navigateToDetail: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    if (state.init) viewModel.setModifiedForm(form.second)
    else if (state.loading) LoadingScreen(text = stringResource(id = R.string.loading))
    else{
        Scaffold(
            modifier = Modifier.padding(horizontal = 15.dp, vertical = 5.dp),
            topBar = {
                TopBar(title = R.string.modify_form, signOutClicked = navigateToLogin)
            },
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
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceAround
            )
            {
                CustomTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    placeholder = { Text(text = stringResource(id = R.string.add_title)) },
                    singleLine = true,
                    value = state.name,
                    onValueChange = viewModel::updateTitle
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp)),
                        onClick = { state.fields.add("") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(id = R.color.modBtn_color),
                            contentColor = colorResource(id = R.color.black)
                        ),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Image(
                            modifier = Modifier.fillMaxSize(),
                            painter = painterResource(id = R.drawable.ic_add_project),
                            contentScale = ContentScale.Crop,
                            contentDescription = "Add field"
                        )
                    }
                }
                LazyColumn(
                    modifier = Modifier
                        .padding(vertical = 5.dp)
                        .fillMaxWidth()
                        .height(490.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    itemsIndexed(state.fields) { index, data ->
                        Spacer(modifier = Modifier.size(10.dp))
                        FormField(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(colorResource(id = R.color.gray_color)),
                            fieldName = data,
                            onFieldNameChange = { state.fields[index] = it },
                            onDeleteClicked = { state.fields.remove(data) }
                        )
                    }
                }

                if (state.fields.size > 0) {
                    CustomButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 25.dp, vertical = 10.dp)
                            .height(50.dp),
                        text = stringResource(id = R.string.save_button),
                        textSize = 16.sp,
                        background = colorResource(id = R.color.smooth_blue),
                        border = BorderStroke(0.dp, Color.Transparent),
                        action = {
                            viewModel.submit(form, projectId, navigateToDetail)
                        }
                    )
                }
            }
        }
    }
}

