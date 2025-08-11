package com.thor.swimtracker.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.thor.swimtracker.data.NumberViewModel
import com.thor.swimtracker.notifications.scheduleNotificationAt
import com.thor.swimtracker.screens.components.graph.LineChart
import com.thor.swimtracker.screens.components.graph.NumberEntryUi

@Composable
fun HomeScreen(
    onNavigate: () -> Unit,
    viewModel: NumberViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val entries by viewModel.entries.collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text("Swim Tracker:")

        LineChart(
            entries = entries.map { NumberEntryUi(it.value, it.date) },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = true)
        )

        Button(onClick = onNavigate) {
            Text("Add Swim")
        }

//        val context = LocalContext.current
//        Button(onClick = {
//            com.thor.swimtracker.notifications.NotificationHelper.ensureChannel(context)
//            com.thor.swimtracker.notifications.NotificationHelper.sendNow(
//                context,
//                "Test from Home",
//                "If you see this, notifications work 🎉"
//            )
//        }) { Text("Post test notification now") }
        val context = LocalContext.current

//        val now = java.util.Calendar.getInstance()
//        val currentHour = now.get(java.util.Calendar.HOUR_OF_DAY)
//        val currentMinute = now.get(java.util.Calendar.MINUTE)
//
//        for (i in 0..9) {
//            val targetMinute = (currentMinute + i) % 60
//            val targetHour = (currentHour + (currentMinute + i) / 60) % 24

//        scheduleNotificationAt(context,targetHour,targetMinute,"the fukcing title?","do it work!")
//        }
        scheduleNotificationAt(context,12,0,"Did you swim?","If so pls fill in!")
        scheduleNotificationAt(context,19,0,"Did you swim?","If so pls fill in!")

//        Button(onClick = { viewModel.addTestEntries() }) {
//            Text("Add Test Entries")
//        }
    }
}
