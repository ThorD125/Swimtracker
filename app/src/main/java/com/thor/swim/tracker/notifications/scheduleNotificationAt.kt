package com.thor.swim.tracker.notifications

import android.content.Context
import android.util.Log
import androidx.work.*
import java.time.Month
import java.util.*
import java.util.concurrent.TimeUnit

fun scheduleNotificationAt(
    context: Context,
    month: Int,
    day: Int,
    hour: Int,
    minute: Int,
    title: String,
    text: String,
) {
    val cal = Calendar.getInstance().apply {
        timeInMillis = System.currentTimeMillis()
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)

        add(Calendar.MONTH, month)
        add(Calendar.DAY_OF_MONTH, day)

        if (get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
            add(Calendar.DAY_OF_MONTH, 1)
        }
        if (get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            add(Calendar.DAY_OF_MONTH, 1)
        }
    }

    val delay = cal.timeInMillis - System.currentTimeMillis()
    if (delay <= 0) {
        Log.w("scheduleNotificationAt", "Scheduled time already passed, skipping")
        return
    }

    val data = workDataOf(
        "title" to title,
        "text" to text
    )

    val request = OneTimeWorkRequestBuilder<NotificationWorker>()
        .setInitialDelay(delay, TimeUnit.MILLISECONDS)
        .setInputData(data)
        .addTag("notif_${cal.get(Calendar.MONTH)}_${cal.get(Calendar.DAY_OF_MONTH)}_${hour}_${minute}") // use tag for cancellation/listing
        .build()

    WorkManager.getInstance(context).enqueue(request)

    Log.d("scheduleNotification", "Scheduled notification at ${cal.time}")
}

fun cancelScheduledNotification(context: Context, month: Int, day: Int, hour: Int, minute: Int) {
    val tag = "notif_${month}_${day}_${hour}_${minute}"
    WorkManager.getInstance(context).cancelAllWorkByTag(tag)
    Log.d("scheduleNotificationcancel", "Cancelled notification with tag $tag")
}

fun listScheduledNotifications(context: Context) {
    val workInfos = WorkManager.getInstance(context)
        .getWorkInfosByTag("notif") // or fetch all then filter
        .get()
    Log.d("listScheduledNotifications", "Work: info.id, state=info.state")
    for (info in workInfos) {
        Log.d("listScheduledNotifications", "Work: ${info.id}, state=${info.state}")
    }
}
