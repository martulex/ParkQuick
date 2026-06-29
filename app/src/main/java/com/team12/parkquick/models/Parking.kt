package com.team12.parkquick.models

import androidx.room3.Entity
import androidx.room3.PrimaryKey
import java.time.LocalDateTime

@Entity
data class Parking(

    @PrimaryKey
    val id: String,

    val name: String,
    val notes: String? = null,

    // Standort
    val latitude: Double,
    val longitude: Double,

    // Bilder
    val imageUrls: List<String> = emptyList(),

    // Parkzeitraum
    val parkTime: LocalDateTime,
    val pickupTime: LocalDateTime,

    val isInParking: Boolean
)