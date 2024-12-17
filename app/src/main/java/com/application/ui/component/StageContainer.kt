package com.application.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.application.R

@Composable
fun StageContainer(
    modifier: Modifier = Modifier,
    title: String,
    description: String? = null,
) {
    Card(
        modifier = modifier
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(15.dp),
                spotColor = Color.Black,
                ambientColor = Color.Black
            )
            .wrapContentSize()
            .fillMaxWidth(),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(id = R.color.white)
        ),
    ) {
        Column(
            modifier = Modifier.padding(15.dp),
        ) {
            Text(
                modifier = Modifier.padding(bottom = 7.dp),
                text = title,
                fontSize = 16.sp,
                color = colorResource(id = R.color.main_green)
            )
            description?.let {
                Text(
                    text = it,
                    fontSize = 15.sp,
                    color = colorResource(id = R.color.black)
                )
            }
        }
    }
}