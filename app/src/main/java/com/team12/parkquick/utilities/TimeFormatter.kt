package com.team12.parkquick.utilities

import com.team12.parkquick.models.Parking
import java.time.LocalDateTime
import java.time.Duration
import java.time.format.DateTimeFormatter
import java.util.Locale

object TimeFormatter {
    private val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.")
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    private val hourOnlyFormatter = DateTimeFormatter.ofPattern("HH")

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

    fun formatTimeOnly(dateTime: LocalDateTime): String {
        return dateTime.format(formatter)
    }

    fun formatHistoryInfo(parking: Parking): String {
        val start = parking.parkTime
        val end = parking.pickupTime

        return if (start.toLocalDate() == end.toLocalDate()) {
            // Gleicher Tag: "Parked on 21.05. from 11:00 till 12:00"
            "Parked on ${start.format(dateFormatter)} from ${start.format(timeFormatter)} till ${end.format(timeFormatter)}"
        } else {
            // Verschiedene Tage: "Parked from 21.06. 17:12 until 22.06. 10:00"
            "Parked from ${start.format(dateFormatter)} ${start.format(timeFormatter)} until ${end.format(dateFormatter)} ${end.format(timeFormatter)}"
        }
    }
}