package com.team12.parkquick.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.team12.parkquick.models.Parking
import java.time.LocalDateTime

class ParkingViewModel : ViewModel() {
    private val _parkings = MutableLiveData<List<Parking>>(emptyList())
    val parkings: LiveData<List<Parking>> = _parkings
    init {
        //Initializing the data
        loadSampleData()
    }

    private fun loadSampleData() {
        _parkings.value = listOf(
            Parking(
                id = "3",
                name = "Bahnhof Gummersbach",
                latitude = 51.0260,
                longitude = 7.5660,
                parkTime = LocalDateTime.now(),
                pickupTime = LocalDateTime.now().plusHours(6)
            )
        )
    }

    fun addParking(parking: Parking) {
        val currentList = _parkings.value ?: emptyList()
        _parkings.value = currentList + parking
    }
}