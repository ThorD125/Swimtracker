package com.thor.swim.tracker.screens

import android.annotation.SuppressLint
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.thor.swim.tracker.data.NumberViewModel
import com.thor.swim.tracker.notifications.cancelScheduledNotification
import com.thor.swim.tracker.notifications.scheduleNotificationAt

@SuppressLint("MissingPermission")
@Composable
fun TestScreen(
    viewModel: NumberViewModel = viewModel(),
) {
    Button(onClick = { viewModel.addTestEntries() }) {
        Text("Add Test filteredEntries")
    }
    
    Button(onClick = { viewModel.addPreviousWeakEntries() }) {
        Text("Add Test addPreviousWeakEntries")
    }

    val context = LocalContext.current
//    Button(onClick = {
//        NotificationHelper.ensureChannel(context)
//        NotificationHelper.sendNow(
//            context,
//            "Test from Home",
//            "If you see this, notifications work 🎉"
//        )
//    }) { Text("Post test notification now") }

    val now = java.util.Calendar.getInstance()
    val currentHour = now.get(java.util.Calendar.HOUR_OF_DAY)
    val currentMinute = now.get(java.util.Calendar.MINUTE)

    val i = 0
//    for (i in 0..10) {

    val targetMinute = (currentMinute + i) % 60
    val targetHour = (currentHour + (currentMinute + i) / 60) % 24

    scheduleNotificationAt(
        context,
        0,
        targetHour,
        targetMinute,
        "the fucking title?",
        "do it work!"
    )

//    }
    // We scheduled for i in 0..10; cancel the odd ones (1,3,5,...)
    for (i in 0..10) {
//        if (i % 2 == 1) {
        val targetMinute = (currentMinute + i) % 60
        val targetHour = (currentHour + (currentMinute + i) / 60) % 24
        cancelScheduledNotification(context, targetHour, targetMinute)
//        }
    }

}
