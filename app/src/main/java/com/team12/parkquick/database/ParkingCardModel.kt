package com.team12.parkquick.database

import android.location.Location
import androidx.room3.Entity
import androidx.room3.PrimaryKey
import java.util.UUID


@Entity
data class ParkingCard (

    @PrimaryKey
    val id : String = UUID.randomUUID().toString(),

    var name : String = "",
    var price : Float = 0.0f,
    var description : String = "",
    var image : String = "",
    var amountOfSpots : Int = 1,
    var isSharedWithCommunity : Boolean = false,
    //var parkingSpotIsActive : Boolean = false,
    var openTime : String = "00:00",
    var closeTime : String = "23:59",
    val parkingTimeStart : Long = System.currentTimeMillis(),
    val parkingTimeEnd : Long = 0L,
    val isInParking : Boolean = true,
    val isInDiscover : Boolean = false,

    // Standort
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,

    )

// Hilfsfunktion: Berechnet die Live-Entfernung zum Nutzer
fun ParkingCard.getDistanceInKm(userLat: Double, userLon: Double): String {
    val results = FloatArray(1)
    Location.distanceBetween(userLat, userLon, this.latitude, this.longitude, results)
    val distanceKm = results[0] / 1000

    // Gibt die Entfernung mit einer Nachkommastelle zurück
    return String.format("%.1f km", distanceKm)
}