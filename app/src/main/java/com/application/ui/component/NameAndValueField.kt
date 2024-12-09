package com.application.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

@Composable
fun NameAndValueField(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    fieldName: String,
    fieldNameSize: TextUnit = 16.sp,
    fieldNameWeight: FontWeight = FontWeight.Bold,
    fieldNameColor: Color = Color.White,
    fieldValue: String,
    fieldValueSize: TextUnit = 16.sp,
    fieldValueWeight: FontWeight = FontWeight.Normal,
    fieldValueColor: Color = Color.White,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = fieldName,
            fontSize = fieldNameSize,
            fontWeight = fieldNameWeight,
            color = fieldNameColor
        )
        Text(
            text = fieldValue,
            fontSize = fieldValueSize,
            fontWeight = fieldValueWeight,
            color = fieldValueColor
        )
    }
}