package com.application.ui.component

import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import com.application.R

@Composable
fun CustomSnackBarHost(
    snackBarHostState: SnackbarHostState,
    dismissAction: @Composable (() -> Unit)? = null
) {
    SnackbarHost(hostState = snackBarHostState) {
        Snackbar(
            containerColor = colorResource(id = R.color.red),
            dismissAction = dismissAction
        ) {
            val visuals = snackBarHostState.currentSnackbarData?.visuals
            val message = visuals?.message
            Text(text = message ?: "")
        }
    }
}