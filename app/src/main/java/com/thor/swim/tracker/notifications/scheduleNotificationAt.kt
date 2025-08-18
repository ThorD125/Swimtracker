package com.thor.swim.tracker.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.Calendar
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.core.net.toUri
import java.util.Locale

fun scheduleNotificationAt(
    context: Context,
    day: Int,
    hour: Int,
    minute: Int,
    title: String,
    text: String,
) {
    val am = context.getSystemService(AlarmManager::class.java)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
        !am.canScheduleExactAlarms()
    ) {
        val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
            data = "package:${context.packageName}".toUri()
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
        return
    }

    val cal = Calendar.getInstance().apply {
        timeInMillis = System.currentTimeMillis()
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)

        add(Calendar.DAY_OF_MONTH, day)

        if (get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
            add(Calendar.DAY_OF_MONTH, 1)
        }
        if (get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            add(Calendar.DAY_OF_MONTH, 1)
        }
    }

    Log.d("scheduleNotificationAt", "cal: $cal")
    val dayOfWeekShort =
        cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault())
    Log.d("MyTag", "Scheduled notification at ${cal.time} ($dayOfWeekShort)")

    val intent = Intent(context, NotificationReceiver::class.java).apply {
        putExtra("title", title)
        putExtra("text", text)
    }

    val pi = PendingIntent.getBroadcast(
        context,
        hour * 100 + minute,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.timeInMillis, pi)
}

fun cancelScheduledNotification(context: Context, hour: Int, minute: Int) {
    val am = context.getSystemService(AlarmManager::class.java)

    val intent = Intent(context, NotificationReceiver::class.java)

    val pi = PendingIntent.getBroadcast(
        context,
        hour * 100 + minute,
        intent,
        PendingIntent.FLAG_NO_CREATE or
                PendingIntent.FLAG_IMMUTABLE
    )

    if (pi != null) {
        am.cancel(pi)
        pi.cancel()
    }
}
