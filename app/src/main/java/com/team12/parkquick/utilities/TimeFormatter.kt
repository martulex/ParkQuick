package com.team12.parkquick.utilities

import com.team12.parkquick.database.ParkingCard
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TimeFormatter {
    // Unsere Formatierer für Millisekunden
    private val dateFormatter = SimpleDateFormat("dd.MM.", Locale.getDefault())
    private val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())
    private val fullFormatter = SimpleDateFormat("dd.MM. HH:mm", Locale.getDefault())

    fun formatRemainingTime(parkTimeMillis: Long, pickupTimeMillis: Long): String {
        val durationMillis = pickupTimeMillis - parkTimeMillis

        if (durationMillis <= 0) return "0 Minutes"

        val days = durationMillis / (1000 * 60 * 60 * 24)
        val hours = (durationMillis / (1000 * 60 * 60)) % 24
        val minutes = (durationMillis / (1000 * 60)) % 60

        return when {
            days > 0 -> "$days Days $hours Hours"
            hours > 0 -> "$hours Hours $minutes Minutes"
            else -> "$minutes Minutes"
        }
    }

    fun formatParkingInfo(parking: ParkingCard, isActive: Boolean): String {
        val start = fullFormatter.format(Date(parking.parkingTimeStart))
        val end = fullFormatter.format(Date(parking.parkingTimeEnd))

        return if (isActive) {
            "Parked since: \n$start"
        } else {
            "$start – $end"
        }
    }

    fun formatTimeOnly(timeMillis: Long): String {
        return fullFormatter.format(Date(timeMillis))
    }

    fun formatHistoryInfo(parking: ParkingCard): String {
        val startDate = Date(parking.parkingTimeStart)
        val endDate = Date(parking.parkingTimeEnd)

        val startDay = dateFormatter.format(startDate)
        val endDay = dateFormatter.format(endDate)

        val startTime = timeFormatter.format(startDate)
        val endTime = timeFormatter.format(endDate)

        return if (startDay == endDay) {
            // Gleicher Tag: "Parked on 21.05. from 11:00 till 12:00"
            "Parked on $startDay from $startTime till $endTime"
        } else {
            // Verschiedene Tage: "Parked from 21.06. 17:12 until 22.06. 10:00"
            "Parked from $startDay $startTime until $endDay $endTime"
        }
    }
}