package com.thor.swim.tracker.screens

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.thor.swim.tracker.data.NumberViewModel
import com.thor.swim.tracker.notifications.cancelScheduledNotification
import com.thor.swim.tracker.notifications.listScheduledNotifications
import com.thor.swim.tracker.notifications.scheduleNotificationAt
import java.time.LocalDate
import java.util.Calendar

@SuppressLint("MissingPermission")
@Composable
fun TestScreen(
    viewModel: NumberViewModel = viewModel(),
) {
//    Button(onClick = { viewModel.addTestEntries() }) {
//        Text("Add Test filteredEntries")
//    }

    Button(onClick = { viewModel.addPreviousWeakEntries() }) {
        Text("Add Test addPreviousWeakEntries")
    }
    val context = LocalContext.current
    val channel = NotificationChannel(
        "swim_channel",
        "Swim Notifications",
        NotificationManager.IMPORTANCE_HIGH
    )
    val manager = context.getSystemService(NotificationManager::class.java)
    manager.createNotificationChannel(channel)

//    Button(onClick = {
//        NotificationHelper.ensureChannel(context)
//        NotificationHelper.sendNow(
//            context,
//            "Test from Home",
//            "If you see this, notifications work 🎉"
//        )
//    }) { Text("Post test notification now") }

    val now = Calendar.getInstance()
    val currentHour = now.get(Calendar.HOUR_OF_DAY)
    val currentMinute = now.get(Calendar.MINUTE)

//    val i = 0
    for (i in 0..10) {

        val targetMinute = (currentMinute + i) % 60
        val targetHour = (currentHour + (currentMinute + i) / 60) % 24

        scheduleNotificationAt(
            context,
            0,
            0,
            targetHour,
            targetMinute,
            "the fucking title?",
            "do it work!"
        )

    }
    val today = LocalDate.now()

    val day = today.dayOfMonth   // int (1–31)
    val month = today.monthValue // int (1–12)

    // We scheduled for i in 0..10; cancel the odd ones (1,3,5,...)
    for (i in 0..10) {
        if (i % 2 == 1) {
            val targetMinute = (currentMinute + i) % 60
            val targetHour = (currentHour + (currentMinute + i) / 60) % 24
            cancelScheduledNotification(context, month, day, targetHour, targetMinute)
        }
    }


    listScheduledNotifications(context)

}
