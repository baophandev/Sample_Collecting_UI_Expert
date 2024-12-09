package com.application.ui.component

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.application.R

@Composable
fun FieldProject(
    modifier: Modifier = Modifier,
    name: String? = null,
    description: String? = null,
    owner: String? = null,
    thumbnail: Uri? = null
) {
    val context = LocalContext.current

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = Color.Black
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 5.dp,
            pressedElevation = 10.dp
        ),
        shape = RoundedCornerShape(10.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(.95f)
                .height(200.dp)
                .padding(start = 5.dp, end = 5.dp, top = 15.dp)
                .align(Alignment.CenterHorizontally),
            shape = RoundedCornerShape(10.dp)
        ) {
            if (thumbnail != null) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(thumbnail)
                        .build(),
                    modifier = Modifier.fillMaxSize(),
                    contentDescription = "Thumbnail",
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.sample_default),
                    modifier = Modifier.fillMaxSize(),
                    contentDescription = "Thumbnail",
                    contentScale = ContentScale.FillBounds
                )
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 25.dp, end = 25.dp, top = 5.dp, bottom = 5.dp),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = name ?: "Title",
                fontSize = 24.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.W500
            )
            Text(text = owner ?: "Owner", fontSize = 16.sp)
            Text(text = description ?: "No description", fontSize = 16.sp)
        }
    }

}