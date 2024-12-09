package com.application.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.application.R
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDatePicker(
    modifier: Modifier = Modifier,
    fieldName: String = "Date",
    initValue: String? = null,
    pickerTitle: String = "Select date",
    isError: Boolean = false,
    onDateChange: (String) -> Unit
) {
    val dateFormatter =
        DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneOffset.UTC)
    val defaultDateText = initValue ?: stringResource(id = R.string.default_date)

    val state = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(defaultDateText) }

    Column(modifier = modifier) {
        Text(
            modifier = Modifier.padding(start = 10.dp),
            text = fieldName,
            fontWeight = FontWeight.W400,
            fontSize = 15.sp
        )

        Spacer(modifier = Modifier.size(5.dp))

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = Color.Black
            ),
            border = if (isError) BorderStroke(
                width = 2.dp,
                color = colorResource(id = R.color.red)
            )
            else BorderStroke(
                width = 0.dp,
                color = Color.Transparent
            )
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 5.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    modifier = Modifier.padding(start = 5.dp),
                    text = selectedDate,
                    fontWeight = FontWeight.W400,
                    fontSize = 13.sp
                )
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(
                        modifier = Modifier.size(30.dp),
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Date picker"
                    )
                }
            }
        }

        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = {
                    selectedDate = defaultDateText
                    showDatePicker = false
                },
                confirmButton = {
                    CustomButton(
                        text = "OK",
                        textSize = 18.sp,
                        background = colorResource(id = R.color.sky_blue)
                    ) {
                        state.selectedDateMillis?.let {
                            selectedDate = dateFormatter.format(Instant.ofEpochMilli(it))
                            onDateChange(selectedDate)
                            showDatePicker = false
                        }
                    }
                }
            ) {
                DatePicker(
                    state = state,
                    title = {
                        Text(
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                            text = pickerTitle,
                            fontWeight = FontWeight.W500,
                            fontSize = 24.sp
                        )
                    },
                )
            }
        }
    }
}