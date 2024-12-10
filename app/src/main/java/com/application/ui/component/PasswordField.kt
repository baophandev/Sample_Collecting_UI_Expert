package com.application.ui.component

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.application.R

@Composable
fun PasswordField(
    modifier: Modifier = Modifier,
    content: String,
    placeHolderText: String = "",
    onContentChange: (String) -> Unit
) {
    var showPass by remember {
        mutableStateOf(false)
    }
    TextField(
        singleLine = true,
        value = content,
        onValueChange = onContentChange,
        placeholder = {
            Text(text = placeHolderText, color = Color.Gray)
        },
        suffix = {
            IconButton(
                modifier = Modifier
                    .size(18.dp)
                    .padding(vertical = 0.dp),
                onClick = { showPass = !showPass }
            ) {
                Icon(
                    modifier = Modifier.fillMaxSize(.9f),
                    painter = painterResource(id = R.drawable.hide_password_icon),
                    tint = Color.Unspecified,
                    contentDescription = null
                )
            }
        },
        visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
        textStyle = TextStyle.Default.copy(fontSize = 16.sp),
        modifier = modifier.shadow(
            elevation = 10.dp,
            ambientColor = Color.Black,
            spotColor = Color.Black
        ),
        colors = TextFieldDefaults.colors().copy(
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedContainerColor = Color.White,
            focusedContainerColor = Color.White
        ),
        shape = RoundedCornerShape(8.dp),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password
        )
    )
}
