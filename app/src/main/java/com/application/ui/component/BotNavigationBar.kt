package com.application.ui.component

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.application.MainActivity
import com.application.R

@Composable
fun BotNavigationBar(
    modifier: Modifier = Modifier,
    leftButton: @Composable () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(id = R.color.blue_gray)
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 15.dp, vertical = 10.dp)
        ) {
            leftButton()

            Spacer(modifier = Modifier.weight(1f))

            IconButton(
                modifier = Modifier.size(50.dp),
                onClick = {
                    val currentTag = AppCompatDelegate.getApplicationLocales().toLanguageTags()
                    val langTag =
                        if (currentTag == MainActivity.VI_LANG_TAG) MainActivity.EN_LANG_TAG
                        else MainActivity.VI_LANG_TAG
                    MainActivity.changeLanguage(langTag)
                }
            ) {
                Icon(
                    modifier = Modifier.fillMaxSize(.65f),
                    painter = painterResource(id = R.drawable.switch_lang_icon),
                    contentDescription = "Switch language",
                    tint = Color.White
                )
            }
        }
    }
}