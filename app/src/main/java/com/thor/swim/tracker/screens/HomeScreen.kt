package com.thor.swim.tracker.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.stringPreferencesKey
import com.thor.swim.tracker.R
import com.thor.swim.tracker.data.NumberViewModel
import com.thor.swim.tracker.notifications.cancelScheduledNotification
import com.thor.swim.tracker.notifications.scheduleNotificationAt
import com.thor.swim.tracker.screens.components.graph.ChartRange
import com.thor.swim.tracker.screens.components.graph.LineChart
import com.thor.swim.tracker.screens.components.graph.NumberEntryUi
import com.thor.swim.tracker.screens.components.graph.dataStore
import kotlinx.coroutines.flow.map
import java.time.LocalDate

@Composable
fun HomeScreen(
    onNavigate: () -> Unit,
    viewModel: NumberViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val entries by viewModel.entries.collectAsState(initial = emptyList())

    val rangedKeys = stringPreferencesKey("chart_range")

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
                prefs[rangedKeys]
            }
        }.collectAsState(initial = null)

        LineChart(
            entries = entries.map { NumberEntryUi(it.value, it.date) },
        )

        Spacer(modifier = Modifier.height(12.dp))
        Spacer(modifier = Modifier.height(12.dp))

        val filteredEntries = when (lastRange) {
            ChartRange.MONTH.key -> {
                val cutoff = LocalDate.now().minusDays(30)
                entries.filter { entry ->
                    entry.date.isAfter(cutoff) || entry.date.isEqual(cutoff)
                }
            }

            ChartRange.WEEK.key -> {
                val cutoff = LocalDate.now().minusDays(7)
                entries.filter { entry ->
                    entry.date.isAfter(cutoff) || entry.date.isEqual(cutoff)
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
        ) {
            Spacer(Modifier.weight(1f))
            Button(
                onClick = onNavigate
            ) {
                Text(stringResource(R.string.add_a_swim))
            }
        }
    }


    val context = LocalContext.current
    for (i in 0..3) {
        scheduleNotificationAt(
            context,
            i,
            12,
            0,
            stringResource(R.string.notification_title),
            stringResource(R.string.notification_message)
        )
        scheduleNotificationAt(
            context,
            i,
            19,
            0,
            stringResource(R.string.notification_title),
            stringResource(R.string.notification_message)
        )
    }

    LaunchedEffect(entries) {
        if (entries.isNotEmpty()) {
            val lastEntry = entries.maxByOrNull { it.date }!!.date
            val today = LocalDate.now()
            if (lastEntry == today) {
                cancelScheduledNotification(context, 12, 0)
                cancelScheduledNotification(context, 19, 0)
            }
        }
    }
}
