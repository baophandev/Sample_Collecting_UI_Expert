package com.application.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.application.R
import com.application.ui.theme.SampleCollectingApplicationTheme

@Composable
fun FormContainer(
    modifier: Modifier = Modifier,
    isProjectOwner: Boolean = true,
    name: String,
    onModifyClicked: () -> Unit,
    onDeleteClicked: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(10.dp),
                spotColor = Color.Black,
                ambientColor = Color.Black
            )
            .background(color = colorResource(id = R.color.white))
            .clip(RoundedCornerShape(10.dp)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val textWidth = if (isProjectOwner) .8f else 1f

        Text(
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp)
                .fillMaxWidth(textWidth),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            text = name,
            color = Color.Black,
            style = MaterialTheme.typography.bodyLarge,
        )
        Row(
            modifier = Modifier.padding(end = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isProjectOwner) {
                IconButton(
                    modifier = Modifier
                        .size(24.dp)
                        .sizeIn(minWidth = 24.dp, minHeight = 24.dp),
                    onClick = onModifyClicked
                ) {
                    Icon(
                        modifier = Modifier.fillMaxSize(),
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit field",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(
                    modifier = Modifier
                        .size(24.dp)
                        .sizeIn(minWidth = 24.dp, minHeight = 24.dp),
                    onClick = onDeleteClicked
                ) {
                    Icon(
                        modifier = Modifier.fillMaxSize(),
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = colorResource(id = R.color.red)
                    )
                }
            }
        }

    }
}

@Preview(widthDp = 350)
@Composable
private fun FormContainerPreview() {
    SampleCollectingApplicationTheme {
        FormContainer(
            isProjectOwner = true,
            name = "Tên daiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii",
            onModifyClicked = {},
            onDeleteClicked = {}
        )
    }
}