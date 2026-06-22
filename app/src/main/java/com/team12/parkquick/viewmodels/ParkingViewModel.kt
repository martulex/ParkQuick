package com.team12.parkquick.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.map
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.team12.parkquick.models.Parking
import com.team12.parkquick.workers.ParkingEndWorker
import java.time.Duration
import java.time.LocalDateTime
import java.util.UUID
import java.util.concurrent.TimeUnit

class ParkingViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        // Temporäre In-Memory-Schnittstelle für den Worker, solange wir keine DB haben
        private val _staticParkings = MutableLiveData<List<Parking>>(emptyList())

        fun setParkingExpired(id: String) {
            val currentList = _staticParkings.value.orEmpty().map {
                if (it.id == id) it.copy(isInParking = false) else it
            }
            // Da der Worker aus einem Hintergrund-Thread aufruft, nutzen wir postValue
            _staticParkings.postValue(currentList)
        }
    }

    private val _parkings = _staticParkings

    val activeParkings: LiveData<List<Parking>> = _parkings.map { list -> list.filter { it.isInParking } }
    val historyParkings: LiveData<List<Parking>> = _parkings.map { list -> list.filter { !it.isInParking } }

    init {
        if (_parkings.value.orEmpty().isEmpty()) {
            _parkings.value = listOf(
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
                )
            )
            addParking(Parking(
                "3",
                "Bahnhof Gummersbach",
                null,
                51.0260,
                7.5660,
                emptyList(),
                LocalDateTime.now(),
                LocalDateTime.now().plusSeconds(30),
                true
            ))
        }
    }

    fun addParking(parking: Parking) {
        val currentList = _parkings.value ?: emptyList()
        _parkings.value = currentList + parking

        startBackgroundTimer(parking)
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

    fun addNewParkingWithExactTime(pickupTime: LocalDateTime, name: String, notes: String) {
        val newParking = Parking(
            id = UUID.randomUUID().toString(),
            name = name,
            notes = notes,
            latitude = 51.0276,
            longitude = 7.5654,
            parkTime = LocalDateTime.now(),
            pickupTime = pickupTime,
            isInParking = true
        )
        addParking(newParking)
    }

    private fun startBackgroundTimer(parking: Parking) {
        val delayInSeconds = Duration.between(LocalDateTime.now(), parking.pickupTime).seconds
        if (delayInSeconds > 0) {
            val inputData = Data.Builder()
                .putString("PARKING_ID", parking.id)
                .build()

            val endWorkRequest = OneTimeWorkRequestBuilder<ParkingEndWorker>()
                .setInitialDelay(delayInSeconds, TimeUnit.SECONDS)
                .setInputData(inputData)
                .build()

            WorkManager.getInstance(getApplication()).enqueue(endWorkRequest)
        } else {
            // Falls Zeit bereits in der Vergangenheit liegt, sofort umschalten
            setParkingExpired(parking.id)
        }
    }

    fun getParkingByID(id : String) : Parking? {

        return _parkings.value?.find { it.id == id }

    }
}