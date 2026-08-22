package com.team12.parkquick.alarm

import android.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

class ParkingAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val parkingId = intent?.getStringExtra("PARKING_ID")
        val alarmType = intent?.getStringExtra("ALARM_TYPE")

        if (parkingId != null && context != null) {
            showNotification(context, alarmType)
            ParkingAlarmScheduler.setParkingExpired(context, parkingId, alarmType)
        }
    }

    private fun showNotification(context: Context, type: String?) {
        val channelId = "parking_alerts"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Parking Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val (title, text) = when (type) {
            "LEAD_TIME" -> "Parking Reminder" to "Your parking time will expire soon!"
            else -> "Parking Expired" to "Your parking time has run out!"
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}