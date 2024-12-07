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
import com.application.constant.UiStatus
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
    formId: String,
    popBackToLogin: () -> Unit,
    popBackToHome: () -> Unit,
    popBackToDetail: (Boolean) -> Unit,
    navigateToWorkersQuestionScreen: () -> Unit,
    navigateToExpertChatScreen: () -> Unit
) {

    val state by viewModel.state.collectAsState()

    when (state.status) {
        UiStatus.INIT -> {
            viewModel.loadModifiedForm(formId)
            viewModel.loadAllModifiedFields(formId)
        }

        UiStatus.LOADING -> LoadingScreen(text = stringResource(id = R.string.loading))
        UiStatus.SUCCESS -> Scaffold(
            topBar = {
                TopBar(title = R.string.modify_form, signOutClicked = popBackToLogin)
            },
            bottomBar = {
                BotNavigationBar(
                    onWorkersQuestionClick = navigateToWorkersQuestionScreen,
                    onExpertChatsClick = navigateToExpertChatScreen
                ) {
                    IconButton(
                        modifier = Modifier.size(50.dp),

                        onClick = popBackToHome
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
            ) {
                state.form?.let {
                    CustomTextField(
                        modifier = Modifier
                            .fillMaxWidth(.95f)
                            .height(60.dp),
                        placeholder = { Text(text = stringResource(id = R.string.add_title)) },
                        singleLine = true,
                        value = it.title,
                        onValueChange = viewModel::updateTitle
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth(.95f)
                        .padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp)),
                        onClick = { viewModel.addNewField() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(id = R.color.main_green),
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
                        .fillMaxWidth(.95f)
                        .height(490.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    itemsIndexed(state.fields) { index, data ->
                        Spacer(modifier = Modifier.size(10.dp))
                        FormField(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(colorResource(id = R.color.white)),
                            fieldName = data.name,
                            onFieldNameChange = { viewModel.updateFieldName(index, it) },
                            onDeleteClicked = { viewModel.deleteField(index) }
                        )
                    }
                }

                if (state.isFormUpdated || state.addedFieldIds.isNotEmpty() ||
                    state.updatedFieldIds.isNotEmpty() || state.deletedFieldIds.isNotEmpty()
                ) {
                    CustomButton(
                        modifier = Modifier
                            .fillMaxWidth(.95f)
                            .padding(horizontal = 25.dp, vertical = 10.dp)
                            .height(50.dp),
                        text = stringResource(id = R.string.save_button),
                        textSize = 16.sp,
                        background = colorResource(id = R.color.main_green),
                        border = BorderStroke(0.dp, Color.Transparent),
                        action = {
                            viewModel.submit(successHandler = popBackToDetail)
                        }
                    )
                }
            }
        }

        UiStatus.ERROR -> TODO()
    }
}
