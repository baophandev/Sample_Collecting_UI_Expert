package com.application.ui.screen

import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.application.R
import com.application.data.entity.Project
import com.application.ui.component.BotNavigationBar
import com.application.ui.component.CustomDatePicker
import com.application.ui.component.CustomTextField
import com.application.ui.component.FieldToList
import com.application.ui.component.LoadingScreen
import com.application.ui.component.RegexValidation
import com.application.ui.component.TopBar
import com.application.ui.viewmodel.ModifyProjectViewModel

@Composable
fun ModifyProjectScreen(
    viewModel: ModifyProjectViewModel = hiltViewModel(),
    project: Project,
    thumbnailUri: Uri? = null,
    navigateToLogin: () -> Unit,
    navigateToHome: () -> Unit,
    navigateToDetail: () -> Unit,
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()

    val pickPictureLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { imageUri ->
        if (imageUri != null) {
            context.contentResolver
                .query(imageUri, null, null, null).use { cursor ->
                    val nameIndex = cursor?.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    cursor?.moveToFirst()
                    nameIndex?.let {
                        val fileName = cursor.getString(it)
                        viewModel.updateThumbnail(Pair(fileName, imageUri))
                    }
                }
        }
    }
    if (state.init) viewModel.setModifiedProject(project, thumbnailUri)
    else if (state.loading) LoadingScreen(text = stringResource(id = R.string.loading))
    else {
        Scaffold(
            modifier = Modifier.padding(horizontal = 15.dp, vertical = 5.dp),
            topBar = { TopBar(title = R.string.modify_project, signOutClicked = navigateToLogin) },
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
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = colorResource(id = R.color.gray_color)
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 10.dp,
                            pressedElevation = 12.dp
                        ),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(
                            horizontal = 0.dp,
                            vertical = 0.dp
                        ),
                        onClick = { pickPictureLauncher.launch("image/*") }
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(colorResource(id = R.color.gray_color)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .zIndex(1f)
                                    .height(40.dp)
                                    .width(40.dp), // Ensure add button is on top
                                painter = painterResource(id = R.drawable.ic_add_project),
                                contentDescription = "add button"
                            )
                            state.thumbnailPath?.second?.let {
                                AsyncImage(
                                    model = ImageRequest.Builder(context).data(it).build(),
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .zIndex(0f), // Place background image below
                                    contentScale = ContentScale.Crop,
                                    contentDescription = "Thumbnail",
                                )
                            }
                        }
                    }
                }
                CustomTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    placeholder = { Text(text = stringResource(id = R.string.add_title)) },
                    singleLine = true,
                    value = state.title,
                    onValueChange = viewModel::updateTitle
                )
                CustomTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    placeholder = { Text(text = stringResource(id = R.string.sample_description_default)) },
                    value = state.description,
                    onValueChange = viewModel::updateDescription
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CustomDatePicker(
                        fieldName = stringResource(id = R.string.start_date),
                        initValue = state.startDate,
                        modifier = Modifier.width(160.dp)
                    ) { viewModel.updateDate(date = it, isStartDate = true) }
                    CustomDatePicker(
                        fieldName = stringResource(id = R.string.end_date),
                        initValue = state.endDate,
                        modifier = Modifier.width(160.dp)
                    ) { viewModel.updateDate(date = it, isStartDate = false) }
                }

                if (state.emailMembers.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .border(
                                width = 2.dp,
                                Color.LightGray,
                                shape = RoundedCornerShape(20.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(id = R.string.no_members),
                            textAlign = TextAlign.Center,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                } else {
                    FieldToList(
                        fieldDataList = state.emailMembers,
                        textValidator = { email ->
                            email.contains(RegexValidation.EMAIL)
                        }
                    )
                }
                // Luu thong tin sau chinh sua
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 6.dp,
                        pressedElevation = 8.dp
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = R.color.modBtn_color),
                        contentColor = colorResource(id = R.color.black)
                    ),
                    onClick = {
                        viewModel.submit(preProject = project, successHandler = navigateToDetail)
                    }
                ) {
                    Text(color = Color.White, text = stringResource(id = R.string.save_button))
                }
            }
        }
    }
}