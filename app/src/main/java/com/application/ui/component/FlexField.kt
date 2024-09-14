package com.application.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.application.R

@Composable
fun FlexField(
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    fieldName: String,
    onFieldNameChange: (String) -> Unit,
    fieldValue: String,
    onFieldValueChange: (String) -> Unit,
    onDeleteClicked: () -> Unit
) {
    val defaultFieldName = stringResource(id = R.string.default_field_name)
    val defaultFieldValue = stringResource(id = R.string.default_field_value)

    var fieldNameReadOnly by remember { mutableStateOf(true) }
    val fieldNameFocusRequester = remember { FocusRequester() }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = modifier.fillMaxWidth(.9f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    modifier = Modifier
                        .wrapContentWidth()
                        .focusRequester(fieldNameFocusRequester)
                        .onFocusChanged {
                            if (!it.isFocused) fieldNameReadOnly = true
                        },
                    placeholder = {
                        Text(text = defaultFieldName)
                    },
                    value = fieldName,
                    readOnly = fieldNameReadOnly,
                    onValueChange = onFieldNameChange,
                    textStyle = TextStyle(
                        fontWeight = FontWeight.W500,
                        fontSize = 16.sp
                    ),
                    colors = TextFieldDefaults.colors(
                        unfocusedIndicatorColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        unfocusedTextColor = Color.Gray,
                        focusedIndicatorColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        focusedTextColor = Color.Black,
                        focusedLabelColor = Color.Gray,
                        cursorColor = Color.Black,
                        errorLabelColor = Color.Red,
                        errorPlaceholderColor = Color.Red,
                        focusedTrailingIconColor = Color.Black
                    ),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done,
                        showKeyboardOnFocus = true
                    )
                )
                IconButton(
                    onClick = {
                        fieldNameReadOnly = !fieldNameReadOnly
                        if (!fieldNameReadOnly) {
                            fieldNameFocusRequester.requestFocus()
                        }
                    }
                ) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit field"
                    )
                }
            }

            TextField(
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(text = defaultFieldValue)
                },
                value = fieldValue,
                onValueChange = onFieldValueChange,
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    unfocusedTextColor = Color.Gray,
                    focusedContainerColor = Color.Transparent,
                    focusedTextColor = Color.Black,
                    focusedLabelColor = Color.Gray,
                    cursorColor = Color.Black,
                    errorLabelColor = Color.Red,
                    errorPlaceholderColor = Color.Red,
                    focusedTrailingIconColor = Color.Black
                ),
                isError = isError,
            )
        }

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