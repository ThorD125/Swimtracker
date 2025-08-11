package com.thor.swimtracker.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.stringPreferencesKey
import com.thor.swimtracker.R
import com.thor.swimtracker.data.NumberViewModel
import com.thor.swimtracker.notifications.scheduleNotificationAt
import com.thor.swimtracker.screens.components.graph.ChartRange
import com.thor.swimtracker.screens.components.graph.LineChart
import com.thor.swimtracker.screens.components.graph.NumberEntryUi
import com.thor.swimtracker.screens.components.graph.dataStore
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun HomeScreen(
    onNavigate: () -> Unit,
    viewModel: NumberViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val entries by viewModel.entries.collectAsState(initial = emptyList())

    val RANGE_KEY = stringPreferencesKey("chart_range")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Text(stringResource(R.string.swim_tracker))

        Spacer(modifier = Modifier.height(12.dp))

        val appContext = LocalContext.current.applicationContext
        val lastRange by remember {
            appContext.dataStore.data.map { prefs ->
                prefs[RANGE_KEY] // returns String? (or whatever type RANGE_KEY stores)
            }
        }.collectAsState(initial = null)

        LineChart(
            entries = entries.map { NumberEntryUi(it.value, it.date) },
        )

        Spacer(modifier = Modifier.height(12.dp))

//        Text(text = lastRange ?: "")

        Spacer(modifier = Modifier.height(12.dp))

        val filteredEntries = when (lastRange) {
            ChartRange.MONTH.key -> {
                val cutoff = LocalDate.now().minusDays(30)
                entries.filter { entry ->
                    val entryDate =
                        LocalDate.parse(entry.date, DateTimeFormatter.ofPattern("dd_MM_yyyy"))
                    entryDate.isAfter(cutoff) || entryDate.isEqual(cutoff)
                }
            }

            ChartRange.WEEK.key -> {
                val cutoff = LocalDate.now().minusDays(7)
                entries.filter { entry ->
                    val entryDate =
                        LocalDate.parse(entry.date, DateTimeFormatter.ofPattern("dd_MM_yyyy"))
                    entryDate.isAfter(cutoff) || entryDate.isEqual(cutoff)
                }
            }

            ChartRange.ALL.key -> entries
            else -> {
                entries
            }
        }

        val totalDistanceKm = if (filteredEntries.isNotEmpty()) {
            filteredEntries.sumOf { it.value } * 25 / 1000.0
        } else {
            0.0
        }

        Text(
            text = "Total Swum Distance: %.2f km".format(totalDistanceKm)
        )

        val averageDistanceKm = if (filteredEntries.isNotEmpty()) {
            filteredEntries.map { it.value }.average() * 25 / 1000.0
        } else {
            0.0
        }

        Text(
            text = "Average Distance: %.2f km".format(averageDistanceKm)
        )
        val lastDistanceKm = if (filteredEntries.isNotEmpty()) {
            filteredEntries.last().value * 25 / 1000.0
        } else {
            0.0
        }

        Text(
            text = "Last Swum Distance: %.2f km".format(lastDistanceKm)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
//                .background(Color.LightGray)
        ) {
            Spacer(Modifier.weight(1f))
            Button(
                onClick = onNavigate
            ) {
                Text(stringResource(R.string.add_swim))
            }

//            Button(onClick = { viewModel.addTestfilteredEntries() }) {
//                Text("Add Test filteredEntries")
//            }
        }
    }


    val context = LocalContext.current
//    Button(onClick = {
//        com.thor.swimtracker.notifications.NotificationHelper.ensureChannel(context)
//        com.thor.swimtracker.notifications.NotificationHelper.sendNow(
//            context,
//            "Test from Home",
//            "If you see this, notifications work 🎉"
//        )
//    }) { Text("Post test notification now") }
//
//    val now = java.util.Calendar.getInstance()
//    val currentHour = now.get(java.util.Calendar.HOUR_OF_DAY)
//    val currentMinute = now.get(java.util.Calendar.MINUTE)
//
//    for (i in 0..9) {
//        val targetMinute = (currentMinute + i) % 60
//        val targetHour = (currentHour + (currentMinute + i) / 60) % 24
//
//        scheduleNotificationAt(
//            context,
//            targetHour,
//            targetMinute,
//            "the fukcing title?",
//            "do it work!"
//        )
//    }
    scheduleNotificationAt(
        context, 12,
        0,
        stringResource(R.string.notification_title),
        stringResource(R.string.notification_message)
    )
    scheduleNotificationAt(
        context,
        19,
        0,
        stringResource(R.string.notification_title),
        stringResource(R.string.notification_message)
    )

}
