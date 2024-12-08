package com.application.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp

@Composable
fun CustomButton(
    modifier: Modifier = Modifier,
    text: String,
    textSize: TextUnit,
    textColor: Color = Color.White,
    icon: Int? = null,
    background: Color = Color.Blue,
    border: BorderStroke? = null,
    action: () -> Unit,
) {
    Button(
        modifier = modifier,
        contentPadding = PaddingValues(
            top = 10.dp,
            bottom = 10.dp,
            start = 20.dp,
            end = 20.dp
        ),
        border = border,
        colors = ButtonDefaults.buttonColors(
            containerColor = background
        ),
        onClick = action
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Image(
                    modifier = Modifier.size(30.dp),
                    painter = painterResource(id = icon),
                    contentDescription = "Login icon"
                )

                Spacer(modifier = Modifier.size(10.dp))
            }

            Text(
                text = text,
                fontSize = textSize,
                color = textColor
            )
        }
    }
}