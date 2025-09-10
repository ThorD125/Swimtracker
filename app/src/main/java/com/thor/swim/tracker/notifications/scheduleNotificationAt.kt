package com.thor.swim.tracker.notifications

import android.content.Context
import android.util.Log
import androidx.work.*
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

    val tagname =
        "notif_${cal.get(Calendar.MONTH)}_${cal.get(Calendar.DAY_OF_MONTH)}_${hour}_${minute}"

    val workInfos = WorkManager.getInstance(context)
        .getWorkInfosByTag(tagname)
        .get()

    val anyEnqueued = workInfos.any { it.state == WorkInfo.State.ENQUEUED }

    if (!anyEnqueued) {
        val request = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .addTag(tagname)
            .build()

        WorkManager.getInstance(context).enqueue(request)
    }
}

fun cancelScheduledNotification(context: Context, month: Int, day: Int, hour: Int, minute: Int) {
    val tag = "notif_${month}_${day}_${hour}_${minute}"
    WorkManager.getInstance(context).cancelAllWorkByTag(tag)
}

fun listScheduledNotifications(context: Context) {
    val workInfos = WorkManager.getInstance(context)
        .getWorkInfosByTag("notif")
        .get()

    for (info in workInfos) {
        Log.d("listScheduledNotifications", "Work: ${info.id}, state=${info.state}")
    }
}
