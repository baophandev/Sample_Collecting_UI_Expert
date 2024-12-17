package com.application.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.application.ui.theme.SampleCollectingApplicationTheme

@Composable
fun StageContainer(
    modifier: Modifier = Modifier,
    title: String,
    description: String? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(10.dp),
                spotColor = Color.Black,
                ambientColor = Color.Black
            )
            .background(color = MaterialTheme.colorScheme.secondary)
            .clip(RoundedCornerShape(10.dp)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
        ) {
            Text(
                modifier = Modifier.padding(bottom = 6.dp, top = 3.dp),
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleMedium,
            )
            description?.let {
                Text(
                    text = it,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.Black,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Preview(widthDp = 350)
@Composable
private fun StageContainerPreview() {
    SampleCollectingApplicationTheme(dynamicColor = false) {
        StageContainer(
            description = "Tên daiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii",
            title = "Tên daiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii",
        )
    }
}