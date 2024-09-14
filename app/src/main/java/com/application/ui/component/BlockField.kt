package com.application.ui.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.application.R

@Composable
fun BlockField(
    modifier: Modifier = Modifier,
    fieldName: String = "Field name",
    placeHolder: String = "",
    isError: State<Boolean> = mutableStateOf(false),
    onValueChange: (String) -> Unit
) {
    var currentText by remember { mutableStateOf("") }

    TextField(
        modifier = modifier.fillMaxWidth(),
        label = {
            Row {
                Text(text = fieldName)
                Icon(
                    modifier = Modifier.size(8.dp),
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = colorResource(id = R.color.red)
                )
            }
        },
        placeholder = {
            Text(text = placeHolder)
        },
        value = currentText,
        onValueChange = {
            currentText = it
            onValueChange(it)
        },
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color.Transparent,
            unfocusedTextColor = Color.Gray,
            focusedContainerColor = Color.Transparent,
            focusedTextColor = Color.Black,
            focusedLabelColor = Color.Gray,
            cursorColor = Color.Black,
            errorLabelColor = Color.Red,
            errorPlaceholderColor = Color.Red,
        ),
        isError = isError.value,
    )
}