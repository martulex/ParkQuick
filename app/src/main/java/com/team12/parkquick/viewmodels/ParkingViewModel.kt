package com.team12.parkquick.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.team12.parkquick.database.AppRoomDatabase
import com.team12.parkquick.database.ParkingCard
import com.team12.parkquick.database.RoomParkingCardRepository
import com.team12.parkquick.workers.ParkingEndWorker
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import java.util.concurrent.TimeUnit

class ParkingViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppRoomDatabase.getInstance(application)
    private val dao = database.parkingCardDao()
    private val repository = RoomParkingCardRepository(dao)

    // Die Live-Daten aus Room (Flow)
    val allParkings = repository.getAllParkingCards()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeParkings = allParkings.map { list -> list.filter { it.isInParking } }
    val historyParkings = allParkings.map { list -> list.filter { !it.isInParking } }

    fun addNewParking(selectedMinutes: Long, name: String, description: String) {
        viewModelScope.launch {
            val startTime = System.currentTimeMillis()
            val endTime = startTime + (selectedMinutes * 60 * 1000)

            val newSpot = ParkingCard(
                id = UUID.randomUUID().toString(),
                name = name.ifEmpty { "Unbenannter Parkplatz" },
                description = description,
                price = 0f,
                latitude = 50.9,
                longitude = 6.9,
                parkingTimeStart = startTime,
                parkingTimeEnd = endTime,
                isInParking = true, // Ist jetzt aktiv
                image = "",
                amountOfSpots = 5,
                openTime = "08:00",
                closeTime = "23:00",
            )

            // In die Datenbank schreiben
            repository.insert(newSpot)

            // Timer starten
            startBackgroundTimer(newSpot)
        }
    }
    private fun startBackgroundTimer(card: ParkingCard) {
        val currentTime = System.currentTimeMillis()
        val delayInMillis = card.parkingTimeEnd - currentTime

        if (delayInMillis > 0) {
            val delayInSeconds = delayInMillis / 1000

            val inputData = Data.Builder()
                .putString("PARKING_ID", card.id)
                .build()

            val endWorkRequest = OneTimeWorkRequestBuilder<ParkingEndWorker>()
                .setInitialDelay(delayInSeconds, TimeUnit.SECONDS)
                .setInputData(inputData)
                .build()

            WorkManager.getInstance(getApplication()).enqueue(endWorkRequest)
        }
    }
    suspend fun getParkingByID(id: String): ParkingCard? {
        return repository.getParkingCardById(id)
    }

    fun deleteParking(card: ParkingCard) {
        viewModelScope.launch {
            repository.deleteParkingCard(card)
        }
    }
}