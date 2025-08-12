package com.thor.swimtracker.notifications

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.thor.swimtracker.MainActivity
import com.thor.swimtracker.R

object NotificationHelper {
    const val CHANNEL_ID = "swim_reminders"

    fun ensureChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Swim reminders",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply { description = "Reminders to log your swims" }

        val nm = context.getSystemService(NotificationManager::class.java)
        nm.createNotificationChannel(channel)
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun sendNow(context: Context, title: String, text: String) {
        // Tap opens the app
        val tapIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val tapPending = PendingIntent.getActivity(
            context,
            0,
            tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            // Use your own small icon if you have one
            .setSmallIcon(R.drawable.ic_launcher_foreground) // fallback: android.R.drawable.ic_dialog_info
            .setContentTitle(title)
            .setContentText(text)
            .setContentIntent(tapPending)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(
            (System.currentTimeMillis() % Int.MAX_VALUE).toInt(),
            notification
        )
    }
}
