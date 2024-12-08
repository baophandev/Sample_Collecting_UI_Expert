package com.application.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.application.ui.component.CustomCircularProgressIndicator

@Composable
fun LoadingScreen(modifier: Modifier = Modifier, text: String) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(250, 246, 246, 154)),
        contentAlignment = Alignment.Center
    ) {
        CustomCircularProgressIndicator(text = text)
    }
}