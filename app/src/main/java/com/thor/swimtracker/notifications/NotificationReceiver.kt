package com.thor.swimtracker.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.thor.swimtracker.R

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("title") ?: context.getString(R.string.swim_tracker)
        val text = intent.getStringExtra("text") ?: context.getString(R.string.time_to_swim)
        NotificationHelper.ensureChannel(context)
        NotificationHelper.sendNow(context, title, text)
    }
}
