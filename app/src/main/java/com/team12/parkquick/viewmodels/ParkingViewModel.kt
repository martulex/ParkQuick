package com.team12.parkquick.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.team12.parkquick.database.AppRoomDatabase
import com.team12.parkquick.database.ParkingCard
import com.team12.parkquick.database.RoomParkingCardRepository
import com.team12.parkquick.repository.ParkingRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class ParkingViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppRoomDatabase.getInstance(application)
    private val dao = database.parkingCardDao()
    private val repository = RoomParkingCardRepository(dao)

    // Die Live-Daten aus Room (Flow)
    val allParkings: StateFlow<List<ParkingCard>> = repository.getAllParkingCards()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeParkings = allParkings.map { list -> list.filter { it.isInParking } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        
    val historyParkings = allParkings.map { list -> list.filter { !it.isInParking } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /**
     * Creates and adds a new parking entry to the repository.
     *
     * @param minutes Duration of parking in minutes.
     * @param name Name of the parking spot.
     * @param notes Optional notes about the parking spot.
     */
    fun addNewParking(minutes: Long, name: String, notes: String? = null) {
        viewModelScope.launch {
            val startTime = System.currentTimeMillis()
            val endTime = startTime + (minutes * 60 * 1000)

            val newSpot = ParkingCard(
                id = UUID.randomUUID().toString(),
                name = name.ifEmpty { "Unbenannter Parkplatz" },
                description = notes ?: "",
                price = 0f,
                latitude = 50.9, // TODO: Replace with actual GPS coordinates
                longitude = 6.9,
                parkingTimeStart = startTime,
                parkingTimeEnd = endTime,
                isInParking = true,
                image = "",
                amountOfSpots = 1,
                openTime = "00:00",
                closeTime = "23:59",
            )

            // In die Datenbank schreiben
            repository.insert(newSpot)

            // Alarm Manager Timer starten
            ParkingRepository.scheduleParkingAlarm(getApplication(), newSpot)
        }
    }

    suspend fun getParkingByID(id: String): ParkingCard? {
        return repository.getParkingCardById(id)
    }

    fun deleteParking(card: ParkingCard) {
        viewModelScope.launch {
            ParkingRepository.cancelParkingAlarm(getApplication(), card.id)
            repository.deleteParkingCard(card)
        }
    }
}
