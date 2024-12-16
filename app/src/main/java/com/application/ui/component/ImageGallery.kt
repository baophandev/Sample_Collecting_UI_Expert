package com.application.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OnePictureGallery(images: Int, maxWidth: Int, maxHeight: Int, backgroundColor: Color) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(1),
        modifier = Modifier
            .requiredSize(maxWidth.dp, maxHeight.dp)
            .background(backgroundColor)
    ) {
        item {
            Image(
                painter = painterResource(id = images),
                contentDescription = "Image 0",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .requiredSize(maxWidth.dp, maxHeight.dp)
                    .padding(4.dp, 8.dp)
                    .clip(RoundedCornerShape(20.dp))
            )
        }
    }
}

@Composable
fun TwoPicturesGallery(images: List<Int>, maxWidth: Int, maxHeight: Int, backgroundColor: Color) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .requiredSize(maxWidth.dp, maxHeight.dp)
            .background(backgroundColor)
    ) {
        items(images.size) { index ->
            Image(
                painter = painterResource(id = images[index]),
                contentDescription = "Image $index",
                modifier = Modifier
                    .requiredSize((maxWidth / 2).dp, maxHeight.dp)
                    .padding(4.dp, 8.dp)
                    .clip(RoundedCornerShape(20.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
fun ThreePicturesGallery(images: List<Int>, maxWidth: Int, maxHeight: Int, backgroundColor: Color) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.Start,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .requiredSize(maxWidth.dp, maxHeight.dp)
            .background(backgroundColor)
    ) {
        items(images.size, span = { GridItemSpan(if (it == 2) 2 else 1) }) { index ->
            if (index < 2) {
                Image(
                    painter = painterResource(id = images[index]),
                    contentDescription = "Image $index",
                    modifier = Modifier
                        .requiredSize((maxWidth / 2).dp, (maxHeight / 2).dp)
                        .padding(4.dp, 8.dp)
                        .clip(RoundedCornerShape(20.dp)),
                    contentScale = ContentScale.Crop
                )
            } else if (index == 2) {
                Image(
                    painter = painterResource(id = images[2]),
                    contentDescription = "Image 3",
                    modifier = Modifier
                        .requiredSize(maxWidth.dp, (maxHeight / 2).dp)
                        .padding(start = 4.dp, end = 4.dp, top = 0.dp, bottom = 8.dp)
                        .clip(RoundedCornerShape(20.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Composable
fun FourOrMorePicturesGallery(
    images: List<Int>,
    maxWidth: Int,
    maxHeight: Int,
    backgroundColor: Color
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .requiredSize(maxWidth.dp, maxHeight.dp)
            .background(backgroundColor)
    ) {
        items(images.size) { index ->
            if (images.size < 5) {
                Image(
                    painter = painterResource(id = images[index]),
                    contentDescription = "Image $index",
                    modifier = Modifier
                        .requiredSize((maxWidth / 2).dp, (maxHeight / 2).dp)
                        .padding(start = 4.dp, end = 4.dp, top = 3.dp, bottom = 3.dp)
                        .clip(RoundedCornerShape(20.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                if (index < 3) {
                    Image(
                        painter = painterResource(id = images[index]),
                        contentDescription = "Image $index",
                        modifier = Modifier
                            .requiredSize((maxWidth / 2).dp, (maxHeight / 2).dp)
                            .padding(start = 4.dp, end = 4.dp, top = 3.dp, bottom = 3.dp)
                            .clip(RoundedCornerShape(20.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else if (index == 3) {
                    Box(
                        modifier = Modifier
                            .requiredSize((maxWidth / 2).dp, (maxHeight / 2).dp)
                            .padding(start = 4.dp, end = 4.dp, top = 3.dp, bottom = 3.dp)
                            .clip(RoundedCornerShape(20.dp))
                    ) {
                        Image(
                            painter = painterResource(id = images[index]),
                            contentDescription = "Image $index",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .requiredSize((maxWidth / 2).dp, (maxHeight / 2).dp)
                                .padding(start = 4.dp, end = 4.dp, top = 3.dp, bottom = 3.dp)
                                .clip(RoundedCornerShape(20.dp)),
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.5f))
                                .clickable { /*TODO*/ }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                            ) {
                                Text(
                                    text = "+${images.size - 4}",
                                    style = TextStyle(
                                        color = Color.White,
                                        fontSize = 20.sp,
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun ImageGallery(
    images: List<Int>,
    maxWidth: Int = 352,
    maxHeight: Int = 187,
    backgroundColor: Color = Color.White,
) {
    val length = images.count()
    when (length) {
        1 -> OnePictureGallery(images[0], maxWidth, maxHeight, backgroundColor)
        2 -> TwoPicturesGallery(images, maxWidth, maxHeight, backgroundColor)
        3 -> ThreePicturesGallery(images, maxWidth, maxHeight, backgroundColor)
        4 -> FourOrMorePicturesGallery(images, maxWidth, maxHeight, backgroundColor)
        else -> {}
    }
}
