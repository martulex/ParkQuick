package com.team12.parkquick.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.team12.parkquick.database.AppRoomDatabase
import com.team12.parkquick.database.ParkingCard
import com.team12.parkquick.alarm.ParkingAlarmReceiver
import com.team12.parkquick.settings.UserSettingsRepository
import kotlinx.coroutines.flow.first

object ParkingAlarmScheduler {

    private fun getDao(context: Context) = AppRoomDatabase.getInstance(context).parkingCardDao()

    suspend fun scheduleParkingAlarm(context: Context, parking: ParkingCard) {
        val userSettingsRepository = UserSettingsRepository(context)
        val settings = userSettingsRepository.getUserSettings().first()
        val leadTimeMillis = settings.notificationLeadTime * 60 * 1000L

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // 1. Alarm for Expiry
        val expiryTime = parking.parkingTimeEnd
        scheduleAlarm(context, alarmManager, parking.id, expiryTime, "EXPIRY", parking.id.hashCode())

        // 2. Alarm for Lead Time
        if (leadTimeMillis > 0) {
            val leadTimeTrigger = expiryTime - leadTimeMillis
            if (leadTimeTrigger > System.currentTimeMillis()) {
                scheduleAlarm(context, alarmManager, parking.id, leadTimeTrigger, "LEAD_TIME", parking.id.hashCode() + 1)
            }
        }
    }

    private fun scheduleAlarm(
        context: Context,
        alarmManager: AlarmManager,
        parkingId: String,
        triggerAtMillis: Long,
        type: String,
        requestCode: Int
    ) {
        val intent = Intent(context, ParkingAlarmReceiver::class.java).apply {
            putExtra("PARKING_ID", parkingId)
            putExtra("ALARM_TYPE", type)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
            )
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
            )
        }
    }

    fun setParkingExpired(context: Context, id: String, type: String?) {
        // This function will now be responsible for showing notifications
        // instead of updating the database state (except maybe for logging/analytics)
        // Manual termination is now required via the UI
    }

    fun cancelParkingAlarm(context: Context, parkingId: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        
        // Cancel Expiry Alarm
        cancelAlarm(context, alarmManager, parkingId.hashCode())
        
        // Cancel Lead Time Alarm
        cancelAlarm(context, alarmManager, parkingId.hashCode() + 1)
    }

    private fun cancelAlarm(context: Context, alarmManager: AlarmManager, requestCode: Int) {
        val intent = Intent(context, ParkingAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )

        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }
}
