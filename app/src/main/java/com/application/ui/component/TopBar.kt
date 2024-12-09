package com.application.ui.component

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.application.R

@Composable
fun TopBar(
    modifier: Modifier = Modifier,
    onSearchChange: ((String) -> Unit)? = null,
    @StringRes title: Int,
    signOutClicked: () -> Unit
) {
    val searchTitle = stringResource(id = R.string.search_title)

    var currentSearchText by remember { mutableStateOf(searchTitle) }
    var showSignOutButton by remember { mutableStateOf(false) }
    val signOut = stringResource(id = R.string.signed_out)

    Row(
        modifier = modifier
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box {
                    Box {
                        IconButton(onClick = { showSignOutButton = !showSignOutButton }) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Avatar",
                                modifier = Modifier.size(50.dp)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (onSearchChange != null) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(.8f)
                                    .fillMaxHeight(.75f)
                                    .background(
                                        color = Color.LightGray,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .padding(top = 3.dp, bottom = 3.dp, start = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Icon(
                                    modifier = Modifier.size(20.dp),
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search"
                                )
                                BasicTextField(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .fillMaxHeight(.5f)
                                        .onFocusChanged {
                                            if (it.isFocused) {
                                                if (currentSearchText == searchTitle)
                                                    currentSearchText = ""
                                            } else {
                                                if (currentSearchText.isBlank())
                                                    currentSearchText = searchTitle
                                            }
                                        },
                                    singleLine = true,
                                    textStyle = TextStyle(
                                        fontSize = 12.sp,
                                        textAlign = TextAlign.Start,
                                        textIndent = TextIndent(
                                            firstLine = 3.sp,
                                            restLine = 10.sp
                                        )
                                    ),
                                    keyboardOptions = KeyboardOptions(
                                        imeAction = ImeAction.Search
                                    ),
                                    value = currentSearchText,
                                    onValueChange = {
                                        currentSearchText = it
                                        onSearchChange(it)
                                    }
                                )
                            }
                        } else {
                            Row(
                                modifier = Modifier
                                    .background(
                                        color = MaterialTheme.colorScheme.primary,
                                        shape = RoundedCornerShape(20.dp)
                                    )
                                    .padding(horizontal = 15.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = stringResource(id = title)
                                        .uppercase(),
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.W700,
                                    textAlign = TextAlign.Center,
                                    letterSpacing = 2.sp,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    }
                }
            }

            DropdownMenu(
                modifier = Modifier.wrapContentSize(),
                expanded = showSignOutButton,
                onDismissRequest = { showSignOutButton = false }
            ) {
                DropdownMenuItem(
                    modifier = Modifier.size(width = 110.dp, height = 25.dp),
                    onClick = {
                        showSignOutButton = false
                    },
                    text = {
                        Text(
                            color = colorResource(id = R.color.red),
                            text = stringResource(id = R.string.logout_button)
                        )
                    },
                )
            }
        }
    }
}