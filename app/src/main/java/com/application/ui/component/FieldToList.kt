package com.application.ui.component

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.application.R

@Composable
fun FieldToList(
    fieldDataList: List<String>,
    modifier: Modifier = Modifier,
    duplicateData: Boolean = false,
    textFieldHeight: Dp = 60.dp,
    listHeight: Dp = 100.dp,
    textValidator: ((String) -> Boolean)? = null,
    onAddField: (String) -> Unit,
    onRemoveField: (Int) -> Unit,
) {
    val context = LocalContext.current
    val textNotValid = stringResource(id = R.string.text_not_valid)
    var fieldData by remember { mutableStateOf("") }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CustomTextField(
            modifier = Modifier
                .fillMaxWidth(.95f)
                .height(textFieldHeight),
            singleLine = true,
            placeholder = { Text(text = stringResource(id = R.string.add_member_email)) },
            keyboardActions = KeyboardActions(
                onDone = {
                    if (fieldData.isNotBlank() &&
                        (duplicateData || !fieldDataList.contains(fieldData)) &&
                        (textValidator == null || textValidator(fieldData))
                    ) {
                        onAddField(fieldData)
                        fieldData = ""
                    } else Toast.makeText(context, textNotValid, Toast.LENGTH_SHORT).show()
                }
            ),
            value = fieldData,
            onValueChange = { fieldData = it }
        )

        Spacer(modifier = Modifier.size(10.dp))

        LazyColumn(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .fillMaxWidth(.95f)
                .height(listHeight)
                .border(
                    width = 2.dp,
                    color = Color.LightGray,
                    shape = RoundedCornerShape(20.dp)
                )
                .background(color = MaterialTheme.colorScheme.secondary),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(horizontal = 15.dp)
        ) {
            itemsIndexed(fieldDataList) { idx, data ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = data,
                        fontWeight = FontWeight.W400,
                        fontSize = 15.sp
                    )
                    IconButton(onClick = { onRemoveField(idx) }) {
                        Icon(
                            modifier = Modifier.size(25.dp),
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Remove",
                            tint = colorResource(id = R.color.red)
                        )
                    }
                }
            }
        }
    }
}