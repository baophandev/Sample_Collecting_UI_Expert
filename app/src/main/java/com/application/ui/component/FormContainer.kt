package com.application.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.application.R

@Composable
fun FormContainer(
    modifier: Modifier = Modifier,
    name: String,
    onModifyClicked: () -> Unit,
    onDeleteClicked: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(15.dp),
                spotColor = Color.Black,
                ambientColor = Color.Black
            )
            .background(color = colorResource(id = R.color.white))
            .clip(RoundedCornerShape(10.dp)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = name,
            color = colorResource(id = R.color.black),
            fontSize = 16.sp,
            modifier = Modifier
                .padding(start = 15.dp)
                .wrapContentWidth()
        )
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onModifyClicked) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit field",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            onDeleteClicked?.let {
                IconButton(onClick = it) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = colorResource(id = R.color.red)
                    )
                }
            }
        }
    }
}