package com.application.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.application.R
import com.application.data.entity.Stage
import com.application.ui.component.BotNavigationBar
import com.application.ui.component.CustomButton
import com.application.ui.component.CustomDatePicker
import com.application.ui.component.CustomTextField
import com.application.ui.component.FieldToList
import com.application.ui.component.LoadingScreen
import com.application.ui.component.RegexValidation
import com.application.ui.component.TopBar
import com.application.ui.viewmodel.ModifyStageViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModifyStageScreen(
    viewModel: ModifyStageViewModel = hiltViewModel(),
    projectId: String,
    projectEmailMembers: List<String>?,
    stage: Pair<String, Stage>,
    forms: Map<String, String>,
    navigateToLogin: () -> Unit,
    navigateToHome: () -> Unit,
    navigateToStage: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    var expanded by remember { mutableStateOf(false) }

    if (state.init) {
        viewModel.setStage(stage.second)
        if (stage.second.title.isNullOrBlank()) {
            viewModel.updateTitle(stringResource(id = R.string.stage_title))
        }
        if (stage.second.description.isNullOrBlank()) {
            viewModel.updateDescription(stringResource(id = R.string.stage_description))
        }
    } else if (state.loading) LoadingScreen(text = stringResource(id = R.string.loading))
    else {
        Scaffold(
            modifier = Modifier.padding(horizontal = 15.dp, vertical = 5.dp),
            topBar = {
                TopBar(title = R.string.modify_stage, signOutClicked = navigateToLogin)
            },
            bottomBar = {
                BotNavigationBar(
                    modifier = Modifier.padding(vertical = 10.dp)
                ) {
                    IconButton(
                        modifier = Modifier.size(50.dp),

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
            ) {
                CustomTextField(
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(text = stringResource(id = R.string.add_title)) },
                    singleLine = true,
                    value = state.title,
                    onValueChange = viewModel::updateTitle
                )

                CustomTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    placeholder = { Text(text = stringResource(id = R.string.add_description)) },
                    value = state.description,
                    onValueChange = viewModel::updateDescription
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CustomDatePicker(
                        fieldName = stringResource(id = R.string.start_date),
                        modifier = Modifier.width(160.dp),
                        initValue = stage.second.startDate,
                    ) { viewModel.updateDate(date = it, isStartDate = true) }
                    CustomDatePicker(
                        fieldName = stringResource(id = R.string.end_date),
                        modifier = Modifier.width(160.dp),
                        initValue = stage.second.endDate,
                    ) { viewModel.updateDate(date = it, isStartDate = false) }
                }

                ExposedDropdownMenuBox(
                    modifier = Modifier.clip(RoundedCornerShape(15.dp)),
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                ) {
                    TextField(
                        modifier = Modifier
//                            .menuAnchor()
                            .fillMaxWidth(),
                        readOnly = true,
                        value = forms[state.formId] ?: "",
                        onValueChange = {},
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        colors = ExposedDropdownMenuDefaults.textFieldColors(
                            unfocusedIndicatorColor = colorResource(id = R.color.gray_100),
                            focusedIndicatorColor = colorResource(id = R.color.gray_100),
                            unfocusedContainerColor = colorResource(id = R.color.gray_100)
                        ),
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        forms.forEach { form ->
                            DropdownMenuItem(
                                text = { Text(form.value) },
                                onClick = {
                                    expanded = false
                                    viewModel.updateFormId(form.key)
                                },
                            )
                        }
                    }
                }

                projectEmailMembers?.let {
                    if (state.memberIds.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .border(
                                    width = 0.dp,
                                    Color.LightGray,
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .background(colorResource(id = R.color.gray_100)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(id = R.string.no_members),
                                textAlign = TextAlign.Center,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Light
                            )
                        }
                    } else {
                        FieldToList(
                            fieldDataList = state.memberIds,
                            textValidator = { email ->
                                email.contains(RegexValidation.EMAIL) &&
                                        projectEmailMembers.contains(email)
                            },
                            listHeight = 180.dp,
                            onAddField = {},
                            onRemoveField = {}
                        )
                    }
                }

                CustomButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 25.dp, vertical = 5.dp),
                    text = stringResource(id = R.string.save_button),
                    textSize = 16.sp,
                    background = colorResource(id = R.color.smooth_blue),
                    border = BorderStroke(0.dp, Color.Transparent),
                    action = {
                        viewModel.submit(stage, projectId, navigateToStage)
                    }
                )
            }
        }
    }
}