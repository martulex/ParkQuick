package com.team12.parkquick.utilities

import com.team12.parkquick.models.Parking
import java.time.LocalDateTime
import java.time.Duration
import java.time.format.DateTimeFormatter
import java.util.Locale

object TimeFormatter {


    private val formatter = DateTimeFormatter.ofPattern(
        "dd.MM. HH:mm",
        Locale.GERMANY
    )

    fun formatRemainingTime(parkTime: LocalDateTime, pickupTime: LocalDateTime): String {
        val duration = Duration.between(parkTime, pickupTime)

        val days = duration.toDays()
        val hours = duration.toHours() % 24
        val minutes = duration.toMinutes() % 60

        return when {
            days > 0 -> "$days Tage $hours Stunden"
            hours > 0 -> "$hours Stunden $minutes Minuten"
            else -> "$minutes Minuten"
        }
    }

    fun formatParkingInfo(parking: Parking, isActive: Boolean): String {

        val start = parking.parkTime.format(formatter)
        val end = parking.pickupTime.format(formatter)

        return if (isActive) {
            "Geparkt seit: \n$start"
        } else {
            "$start – $end"
        }
    }
}