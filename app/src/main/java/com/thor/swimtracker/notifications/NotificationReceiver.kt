package com.thor.swimtracker.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("title") ?: "Swim Tracker"
        val text = intent.getStringExtra("text") ?: "Time to swim!"
        NotificationHelper.ensureChannel(context)
        NotificationHelper.sendNow(context, title, text)
    }
}
