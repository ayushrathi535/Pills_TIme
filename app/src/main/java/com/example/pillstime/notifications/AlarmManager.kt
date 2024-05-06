package com.example.pillstime.notifications

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.app.PendingIntent.getActivity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.pillstime.R
import com.example.pillstime.model.Medicine
import com.example.pillstime.ui.MainActivity

class AlarmReceiver : BroadcastReceiver() {
    private var notificationManager: NotificationManagerCompat? = null

    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.e("alarm Manager->","inside on receive ")
        val notificationUtils = context?.let { NotificationUtils(it) }
        val notification = notificationUtils?.getNotificationBuilder()?.build()
        notificationUtils?.getManager()?.notify(150, notification)
    }
}