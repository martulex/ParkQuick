package com.team12.parkquick.repository

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.team12.parkquick.database.AppRoomDatabase
import com.team12.parkquick.database.ParkingCard
import com.team12.parkquick.receivers.ParkingAlarmReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object ParkingRepository {

    private fun getDao(context: Context) = AppRoomDatabase.getInstance(context).parkingCardDao()

    fun scheduleParkingAlarm(context: Context, parking: ParkingCard) {
        val triggerAtMillis = parking.parkingTimeEnd
        val delay = triggerAtMillis - System.currentTimeMillis()

        if (delay > 0) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, ParkingAlarmReceiver::class.java).apply {
                putExtra("PARKING_ID", parking.id)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                parking.id.hashCode(),
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
        } else {
            setParkingExpired(context, parking.id)
        }
    }

    fun setParkingExpired(context: Context, id: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val dao = getDao(context)
            val parking = dao.getParkingCardById(id)
            if (parking != null && parking.isInParking) {
                dao.updateParkingCard(parking.copy(isInParking = false))
            }
        }
    }

    fun cancelParkingAlarm(context: Context, parkingId: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ParkingAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            parkingId.hashCode(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )

        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
        }
    }
}
