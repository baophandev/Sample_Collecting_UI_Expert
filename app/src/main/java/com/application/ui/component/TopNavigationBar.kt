package com.application.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun TopNavigationBar(
    modifier: Modifier = Modifier,
    backAction: () -> Unit = {},
    dropdownMenuContent: @Composable ColumnScope.() -> Unit = {}
) {
    var moreMenuExpended by remember {
        mutableStateOf(false)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    )
    {
        // Nút lùi về
        IconButton(
            modifier = Modifier.size(40.dp),
            onClick = backAction
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
//                painter = painterResource(id = R.drawable.leading_icon),
                contentDescription = "Backward",
                modifier = Modifier.fillMaxSize(),
                tint = Color.White
            )
        }

        Box {
            IconButton(
                modifier = Modifier
                    .size(40.dp)
                    .padding(0.dp),
                onClick = { moreMenuExpended = true }
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
//                painter = painterResource(id = R.drawable.more_vertical),
                    contentDescription = "More",
                    modifier = Modifier.fillMaxSize(),
                    tint = Color.White
                )
            }

            DropdownMenu(
                expanded = moreMenuExpended,
                onDismissRequest = { moreMenuExpended = false },
                content = dropdownMenuContent
            )
        }
    }
}