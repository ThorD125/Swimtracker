package com.thor.swimtracker.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.thor.swimtracker.R
import com.thor.swimtracker.data.NumberViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AddSwimScreen(
    onBack: () -> Unit,
    viewModel: NumberViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    var numberText by rememberSaveable { mutableStateOf("") }
    var navigated by remember { mutableStateOf(false) }

    val today = remember {
        SimpleDateFormat("dd_MM_yyyy", Locale.getDefault()).format(Date())
    }

    // Observe database value for today
    val todayEntry by viewModel.entryForDate(today).collectAsState(initial = null)

    LaunchedEffect(todayEntry) {
        if (todayEntry != null && numberText.isEmpty()) {
            numberText = todayEntry?.value?.toString() ?: ""
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(stringResource(R.string.enter_a_number))

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                    val current = numberText.toIntOrNull() ?: 0
                    numberText = (current - 1).toString()
                }
            ) {
                Text("-")
            }

            Spacer(Modifier.width(12.dp))

            TextField(
                value = numberText,
                onValueChange = { input ->
                    if (input.all(Char::isDigit)) numberText = input
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f) // takes all remaining space
            )

            Spacer(Modifier.width(12.dp))

            Button(
                onClick = {
                    val current = numberText.toIntOrNull() ?: 0
                    numberText = (current + 1).toString()
                }
            ) {
                Text("+")
            }
        }

        Spacer(Modifier.height(12.dp))

        Button(
            enabled = !navigated && numberText.isNotBlank(),
            onClick = {
                if (navigated) return@Button
                navigated = true

                val value = numberText.toIntOrNull()
                if (value != null) {
                    val currentDate = SimpleDateFormat("dd_MM_yyyy", Locale.getDefault())
                        .format(Date())
                    viewModel.save(value, currentDate)
                }

                onBack()
            }
        ) {
            Text(stringResource(R.string.save))
        }
    }
}

