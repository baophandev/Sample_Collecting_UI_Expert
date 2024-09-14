package com.application.ui.screen

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.application.R
import com.application.android.utilities.CoreUtility
import com.application.data.entity.User
import com.application.ui.component.CustomButton
import com.application.ui.component.TitleText
import com.application.ui.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    navigateToHome: (User) -> Unit
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()

    val signedIn = stringResource(id = R.string.login_success)
    val noInternet = stringResource(id = R.string.no_internet)
    val loginCancel = stringResource(id = R.string.login_cancel)
    val loginError = stringResource(id = R.string.login_error)

    if (state.loading) {
        if (!CoreUtility.isInternetConnected(context)) {
            Toast.makeText(context, noInternet, Toast.LENGTH_LONG).show()
            viewModel.reInitScreen()
        } else {
            LaunchedEffect(key1 = "autoLogin") {
                viewModel.autoLogin {
                    Toast.makeText(context, signedIn, Toast.LENGTH_SHORT).show()
                    navigateToHome(it)
                }
            }
        }
    } else if (state.error != null) {
        Toast.makeText(
            context,
            stringResource(id = state.error!!),
            Toast.LENGTH_SHORT
        ).show()
    } else if (state.cancel != null) {
        Toast.makeText(
            context,
            stringResource(id = state.cancel!!),
            Toast.LENGTH_SHORT
        ).show()
        viewModel.reInitScreen()
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Image(
            modifier = Modifier
                .blur(2.dp)
                .fillMaxSize(),
            painter = painterResource(id = R.drawable.login_background),
            contentDescription = "Login background",
            contentScale = ContentScale.Crop,
            alpha = .8f
        )

        TitleText(
            text = stringResource(id = R.string.app_name),
            textSize = 40.sp,
            color = Color.hsl(196f, .75f, .57f)
        )

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            CustomButton(
                text = stringResource(id = R.string.login_button),
                textSize = 25.sp,
                textColor = Color.Gray,
                icon = R.drawable.ic_login_button,
                background = Color.White,
                border = BorderStroke(0.dp, Color.Transparent),
                action = {
                    if (!CoreUtility.isInternetConnected(context)) {
                        Toast.makeText(context, noInternet, Toast.LENGTH_LONG).show()
                        return@CustomButton
                    }
                }
            )
        }
    }
}