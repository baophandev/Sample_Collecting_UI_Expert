package com.application.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.application.R
import com.application.ui.component.CustomDatePicker
import com.application.ui.component.CustomTextField
import com.application.ui.component.PasswordField
import com.application.ui.component.TextButton


@Composable
fun RegisterScreen(modifier: Modifier = Modifier) {

    var selectedGender by remember {
        mutableStateOf("")
    }
    val genders = listOf(
        stringResource(id = R.string.male),
        stringResource(id = R.string.female)
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(108, 205, 132, 255),
                        Color(54, 103, 66, 255),

                        ),
                    startY = 500.0f,
                    endY = 1800.0f
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .paddingFromBaseline(130.dp, 0.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.register_name),
                fontSize = 45.sp,
                lineHeight = 48.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .shadow(
                        elevation = 0.dp,
                        ambientColor = Color.Black,
                        spotColor = Color.Black
                    )
                    .fillMaxWidth(.9f)
                    .paddingFromBaseline(0.dp, 50.dp)
            )
            Column(
                modifier = Modifier
                    .fillMaxHeight(.8f)
                    .fillMaxWidth(.85f)
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(10.dp, 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceAround
            ) {
                CustomTextField(
                    modifier = Modifier.height(55.dp),
                    text = stringResource(id = R.string.enter_full_name)
                )
                CustomTextField(
                    modifier = Modifier.height(55.dp),
                    text = stringResource(id = R.string.enter_email)
                )

                CustomDatePicker(
                    modifier = Modifier
                        .height(55.dp),
                    text = stringResource(id = R.string.enter_birthday),
                )

                Row(
                    modifier = Modifier.fillMaxWidth(1f),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.gender),
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    genders.forEach { gender ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = (selectedGender == gender),
                                onClick = { selectedGender = gender },
                                modifier = Modifier.selectable(
                                    selected = selectedGender == gender,
                                    onClick = { selectedGender = gender }
                                ),
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = Color.Gray,
                                    unselectedColor = Color.Gray
                                )
                            )
                            Text(
                                modifier = Modifier.padding(end = 8.dp),
                                text = gender,
                                style = MaterialTheme.typography.bodyMedium,
                                fontSize = 18.sp
                            )
                        }

                    }
                }

                PasswordField(
                    modifier = Modifier.height(55.dp),
                    text = stringResource(id = R.string.enter_password)
                )

                PasswordField(
                    modifier = Modifier.height(55.dp),
                    text = stringResource(id = R.string.re_enter_password)
                )

                TextButton(
                    modifier = Modifier.fillMaxWidth(.9f),
                    text = stringResource(id = R.string.register_button),
                    fontSize = 18.sp,
                ) { TODO("Register clicked") }

                HorizontalDivider(
                    color = Color(178, 183, 179, 255),
                    thickness = 1.dp,
                    modifier = Modifier.fillMaxWidth(.9f)
                )
                Row {
                    Text(text = stringResource(id = R.string.have_account))
                    Spacer(modifier = Modifier.size(5.dp))
                    Text(
                        text = stringResource(id = R.string.login_button),
                        color = Color(45, 198, 83, 255),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { }
                    )
                }

            }
        }
    }
}

//@Preview(heightDp = 800, widthDp = 400, showBackground = true, showSystemUi = true)
//@Composable
//fun RegisterScreenPreview() {
//    RegisterScreen()
//}