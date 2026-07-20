package com.team12.parkquick.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.team12.parkquick.database.AppRoomDatabase
import com.team12.parkquick.database.ParkingCard
import com.team12.parkquick.database.RoomParkingCardRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class DatabaseViewModel(application: Application) : AndroidViewModel(application) {

    // Datenbank und Repository initialisieren
    private val database = AppRoomDatabase.Companion.getInstance(application)
    private val dao = database.parkingCardDao()
    private val repository = RoomParkingCardRepository(dao)
    private val firestore = FirebaseFirestore.getInstance()

    // Remote Karten aus Firebase
    private val _remoteParkingCards = MutableStateFlow<List<ParkingCard>>(emptyList())
    val parkingCards: StateFlow<List<ParkingCard>> = _remoteParkingCards

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    init {
        fetchRemoteParkings()
    }

    fun fetchRemoteParkings() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                val snapshot = firestore.collection("public_parkings").get().await()
                val cards = snapshot.toObjects<ParkingCard>()
                _remoteParkingCards.value = cards
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    // Funktion zum Hinzufügen einer neuen (Test-)Karte
    fun addDummyCard() {
        viewModelScope.launch {
            val neueKarte = ParkingCard(
                name = "Cloud-Parkplatz ${System.currentTimeMillis().toString().takeLast(3)}",
                price = 2.50f,
                description = "Ein super Parkplatz aus der Cloud.",
                image = "https://images.unsplash.com/photo-1596832323822-b6b383a0967b?q=80&w=2070&auto=format&fit=crop",
                latitude = 50.9375 + (Math.random() - 0.5) / 10,
                longitude = 6.9603 + (Math.random() - 0.5) / 10,
                openTime = "08:00",
                closeTime = "18:00",
                isInDiscover = true,
                isInParking = false,
                isSharedWithCommunity = true
            )
            
            // 1. Lokal speichern
            repository.insert(neueKarte)
            
            // 2. In Firebase speichern (wenn öffentlich)
            if (neueKarte.isSharedWithCommunity) {
                try {
                    firestore.collection("public_parkings")
                        .document(neueKarte.id)
                        .set(neueKarte)
                        .await()
                    fetchRemoteParkings() // Liste aktualisieren
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}
