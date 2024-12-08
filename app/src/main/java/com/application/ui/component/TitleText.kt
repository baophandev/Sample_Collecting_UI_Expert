package com.application.ui.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TitleText(
    modifier: Modifier = Modifier,
    text: String,
    textSize: TextUnit,
    color: Color
) {
    Text(
        modifier = modifier.padding(24.dp),
        text = text,
        fontSize = textSize,
        fontWeight = FontWeight.SemiBold,
        textAlign = TextAlign.Center,
        letterSpacing = 2.sp,
        color = color,
        lineHeight = 40.sp
    )
}