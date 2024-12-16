package com.application.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.application.R

@Composable
fun BotNavigationBar(
    modifier: Modifier = Modifier,
    onQuestionsClick: () -> Unit,
    onExpertChatClick: () -> Unit,
    middleButton: @Composable () -> Unit
) {
    Row(
        modifier = modifier
            .background(MaterialTheme.colorScheme.primary)
            .padding(start = 16.dp, end = 16.dp, top = 1.dp, bottom = 1.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
//        IconButton(
//            modifier = Modifier.size(50.dp),
//            onClick = {
//                val currentTag = AppCompatDelegate.getApplicationLocales().toLanguageTags()
//                val langTag =
//                    if (currentTag == MainActivity.VI_LANG_TAG) MainActivity.EN_LANG_TAG
//                    else MainActivity.VI_LANG_TAG
//                MainActivity.changeLanguage(langTag)
//            }
//        ) {
//            Icon(
//                modifier = Modifier.fillMaxSize(.65f),
//                painter = painterResource(id = R.drawable.switch_lang_icon),
//                contentDescription = "Switch language",
//                tint = MaterialTheme.colorScheme.secondary
//            )
//        }

        IconButton(
            onClick = onQuestionsClick,
            modifier = Modifier.size(50.dp)
        ) {
            Icon(
                modifier = Modifier.fillMaxSize(.75f),
                painter = painterResource(id = R.drawable.worker_question_icon),
                contentDescription = "Worker Question screen",
                tint = MaterialTheme.colorScheme.secondary
            )
        }

        middleButton()

        IconButton(
            onClick = onExpertChatClick,
            modifier = Modifier
                .size(50.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_messages),
                contentDescription = "Expert chat screen",
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.fillMaxSize(.85f)
            )
        }

    }
}

