package com.team12.parkquick.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.team12.parkquick.repository.ParkingRepository

class ParkingAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val parkingId = intent?.getStringExtra("PARKING_ID")
        if (parkingId != null && context != null) {
            ParkingRepository.setParkingExpired(context, parkingId)
        }
    }
}