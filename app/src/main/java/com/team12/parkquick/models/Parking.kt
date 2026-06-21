package com.team12.parkquick.models

import java.time.LocalDateTime

data class Parking(
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