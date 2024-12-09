package com.application.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.application.R
import com.application.constant.UiStatus
import com.application.data.entity.Sample
import com.application.ui.component.FullScreenImage
import com.application.ui.component.NameAndValueField
import com.application.ui.viewmodel.SampleDetailViewModel

@Composable
fun SampleDetailScreen(
    viewModel: SampleDetailViewModel = hiltViewModel(),
    sampleId: String,
    navigateToStageDetail: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    var showSampleData by remember { mutableStateOf(false) }

    when (state.status) {
        UiStatus.INIT -> viewModel.loadSample(sampleId)
        UiStatus.LOADING -> LoadingScreen(text = stringResource(id = R.string.loading))
        UiStatus.ERROR -> TODO("Implement ERROR state")
        UiStatus.SUCCESS -> Scaffold { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                ) {
                    FullScreenImage(
                        uri = state.sample?.image!!,
                        onDismiss = navigateToStageDetail,
                        onTapGesture = { showSampleData = !showSampleData }
                    )
                }
                state.sample?.let { sample ->
                    AnimatedVisibility(
                        visible = (showSampleData),
                        enter = fadeIn(animationSpec = tween(400)),
                        exit = fadeOut(animationSpec = tween(400))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0, 0, 0, 153)),
                            contentAlignment = Alignment.BottomStart
                        ) {
                            SampleBox(sample = sample)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SampleBox(
    modifier: Modifier = Modifier,
    sample: Sample
) {
    Column(modifier = modifier.padding(vertical = 10.dp)) {
//            NameAndValueField(
//                modifier = Modifier
//                    .padding(horizontal = 15.dp),
//                fieldName = stringResource(id = R.string.written_by),
//                fieldNameSize = 18.sp,
//                fieldValue = sample.writtenBy!!,
//                fieldValueSize = 18.sp
//            )
        LazyColumn(
            modifier = Modifier
                .padding(horizontal = 15.dp)
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Bottom
        ) {
            items(sample.answers) { answer ->
                Spacer(modifier = Modifier.size(10.dp))
                NameAndValueField(
                    fieldName = answer.field.name + ": ",
                    fieldNameSize = 18.sp,
                    fieldValue = answer.content,
                    fieldValueSize = 18.sp
                )
            }
        }
        sample.dynamicFields?.let { dynamicFields ->
            LazyColumn(
                modifier = Modifier
                    .padding(horizontal = 15.dp)
                    .fillMaxWidth()
                    .wrapContentHeight(),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Bottom
            ) {
                items(dynamicFields) { field ->
                    Spacer(modifier = Modifier.size(10.dp))
                    NameAndValueField(
                        fieldName = field.name + ": ",
                        fieldNameSize = 18.sp,
                        fieldValue = field.value,
                        fieldValueSize = 18.sp
                    )
                }
            }
        }
    }
}