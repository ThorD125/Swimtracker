package com.thor.swimtracker.notifications

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresPermission
import com.thor.swimtracker.R

class NotificationReceiver : BroadcastReceiver() {
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("title") ?: context.getString(R.string.swim_tracker)
        val text = intent.getStringExtra("text") ?: context.getString(R.string.time_to_swim)
        NotificationHelper.ensureChannel(context)
        NotificationHelper.sendNow(context, title, text)
    }
}
