package com.application.ui.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.application.R

@Composable
fun FormField(
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    fieldName: String,
    onFieldNameChange: (String) -> Unit,
    onDeleteClicked: () -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            modifier = Modifier.wrapContentWidth(),
            placeholder = {
                Text(text = stringResource(id = R.string.field_name))
            },
            value = fieldName,
            onValueChange = onFieldNameChange,
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                unfocusedTextColor = Color.Black,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                focusedTextColor = Color.Black,
            ),
            singleLine = true,
            isError = isError,
        )
        IconButton(onClick = onDeleteClicked) {
            Icon(
                modifier = Modifier.size(24.dp),
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                tint = Color(0xFFE53935)
            )
        }
    }
}
