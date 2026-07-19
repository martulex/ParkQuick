package com.team12.parkquick.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.team12.parkquick.database.AppRoomDatabase
import com.team12.parkquick.database.ParkingCard
import com.team12.parkquick.database.RoomParkingCardRepository
import com.team12.parkquick.repository.ParkingRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

class ParkingViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppRoomDatabase.getInstance(application)
    private val dao = database.parkingCardDao()
    private val repository = RoomParkingCardRepository(dao)
    private val firestore = FirebaseFirestore.getInstance()

    // Remote Karten aus Firebase
    private val _remoteParkingCards = MutableStateFlow<List<ParkingCard>>(emptyList())

    // Die Live-Daten aus Room (Flow)
    val localParkings: StateFlow<List<ParkingCard>> = repository.getAllParkingCards()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Alle Parkplätze kombiniert (für die Map-Marker)
    val allAvailableParkings = combine(localParkings, _remoteParkingCards) { local, remote ->
        (local + remote).distinctBy { it.id }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeParkings = localParkings.map { list -> list.filter { it.isInParking } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        
    val historyParkings = localParkings.map { list -> list.filter { !it.isInParking } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        fetchRemoteParkings()
    }

    private fun fetchRemoteParkings() {
        viewModelScope.launch {
            try {
                val snapshot = firestore.collection("public_parkings").get().await()
                _remoteParkingCards.value = snapshot.toObjects<ParkingCard>()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Creates and adds a new parking entry to the repository.
     */
    fun addNewParking(
        minutes: Long, 
        name: String, 
        notes: String? = null, 
        lat: Double, 
        lng: Double, 
        image: String = "",
        price: Float = 0f,
        spots: Int = 1,
        isPublic: Boolean = false
    ) {
        viewModelScope.launch {
            val startTime = System.currentTimeMillis()
            val endTime = startTime + (minutes * 60 * 1000)

            val newSpot = ParkingCard(
                id = UUID.randomUUID().toString(),
                name = name.ifEmpty { "Unbenannter Parkplatz" },
                description = notes ?: "",
                price = price,
                latitude = lat,
                longitude = lng,
                parkingTimeStart = startTime,
                parkingTimeEnd = endTime,
                isInParking = true,
                image = image,
                amountOfSpots = spots,
                isSharedWithCommunity = isPublic,
                openTime = "00:00",
                closeTime = "23:59",
            )

            // In die lokale Datenbank schreiben
            repository.insert(newSpot)

            // In Firebase schreiben, wenn öffentlich
            if (isPublic) {
                try {
                    firestore.collection("public_parkings")
                        .document(newSpot.id)
                        .set(newSpot)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

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

    fun endParking(card: ParkingCard) {
        viewModelScope.launch {
            ParkingRepository.cancelParkingAlarm(getApplication(), card.id)
            repository.updateParkingCard(card.copy(isInParking = false, parkingTimeEnd = System.currentTimeMillis()))
        }
    }
}
