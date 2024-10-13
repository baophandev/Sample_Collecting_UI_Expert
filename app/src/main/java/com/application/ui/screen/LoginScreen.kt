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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.application.ui.component.CustomTextField
import com.application.ui.component.PasswordField
import com.application.ui.component.TextButton

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    navigateToHome: (String) -> Unit,
) {
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
                .paddingFromBaseline(150.dp, 0.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.app_name),
                fontSize = 48.sp,
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
                    .paddingFromBaseline(0.dp, 100.dp)
            )
            Column(
                modifier = Modifier
                    .fillMaxHeight(.65f)
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
                    text = stringResource(id = R.string.enter_email)
                )
                PasswordField(
                    modifier = Modifier.height(55.dp),
                    text = stringResource(id = R.string.enter_password)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(.8f),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = stringResource(id = R.string.forgot_password),
                        color = Color.Blue
                    )
                }
                TextButton(
                    modifier = Modifier.fillMaxWidth(.9f),
                    text = stringResource(id = R.string.login_button),
                    fontSize = 18.sp,
                ) {
                    val userId = "user"
                    navigateToHome(userId)
                }
                HorizontalDivider(
                    color = Color(178, 183, 179, 255),
                    thickness = 1.dp,
                    modifier = Modifier.fillMaxWidth(.9f)
                )
                Row {
                    Text(text = stringResource(id = R.string.no_account))
                    Spacer(modifier = Modifier.size(5.dp))
                    Text(
                        text = stringResource(id = R.string.register_button),
                        color = Color(45, 198, 83, 255),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { }
                    )
                }

            }
        }
    }
}

@Preview(heightDp = 800, widthDp = 400, showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen {}
}