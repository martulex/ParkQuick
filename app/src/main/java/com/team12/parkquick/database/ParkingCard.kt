package com.team12.parkquick.database

import androidx.room3.Entity
import androidx.room3.PrimaryKey
import java.util.UUID


@Entity
data class ParkingCard (

    @PrimaryKey
    val id : String = UUID.randomUUID().toString(),

    var name : String,
    var price : Float,
    var description : String,
    var image : String,

    // Standort

    val latitude: Double,
    val longitude: Double,


    )