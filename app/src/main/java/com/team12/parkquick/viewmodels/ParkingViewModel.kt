package com.team12.parkquick.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.team12.parkquick.models.Parking
import java.time.LocalDateTime
import java.util.UUID

class ParkingViewModel : ViewModel() {
    private val _parkings = MutableLiveData<List<Parking>>(
        listOf(
            Parking(
                "1",
                "Flughafen Köln",
                null,
                50.8659,
                7.1427,
                emptyList(),
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(2),
                false
            ),
            Parking(
                "2",
                "Zuhause Parkplatz",
                null,
                51.0276,
                7.5654,
                emptyList(),
                LocalDateTime.now().minusHours(5),
                LocalDateTime.now().plusHours(10),
                false
            ),
            Parking(
                "3",
                "Bahnhof Gummersbach",
                null,
                51.0260,
                7.5660,
                emptyList(),
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(6),
                true
            ) // Aktiv!
        )
    )

    val activeParkings: LiveData<List<Parking>> = _parkings.map { list -> list.filter {it.isInParking}}
    val historyParkings: LiveData<List<Parking>> = _parkings.map { list -> list.filter {!it.isInParking}}

    init {
        //Initializing the data
        loadSampleData()
    }

    private fun loadSampleData() {
//        _parkings.value = listOf(
//            Parking(
//                id = "3",
//                name = "Bahnhof Gummersbach",
//                latitude = 51.0260,
//                longitude = 7.5660,
//                parkTime = LocalDateTime.now(),
//                pickupTime = LocalDateTime.now().plusHours(6),
//                isInParking = true
//            )
//        )
    }

    fun addParking(parking: Parking) {
        val currentList = _parkings.value ?: emptyList()
        _parkings.value = currentList + parking
    }

    fun addNewParking(durationMinutes : Long, name : String, notes : String) {

        val newParking = Parking(
            id = UUID.randomUUID().toString(),
            name = name,
            notes = notes,
            latitude = 51.0276,
            longitude = 7.5654,
            parkTime = LocalDateTime.now(),
            pickupTime = LocalDateTime.now().plusMinutes(durationMinutes),
            isInParking = true
        )

        addParking(newParking)

    }

    fun getParkingByID(id : String) : Parking? {

        return _parkings.value?.find { it.id == id }

    }
}