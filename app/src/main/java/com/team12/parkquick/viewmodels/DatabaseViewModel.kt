package com.team12.parkquick.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.team12.parkquick.database.AppRoomDatabase
import com.team12.parkquick.database.ParkingCard
import com.team12.parkquick.database.RoomParkingCardRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DatabaseViewModel(application: Application) : AndroidViewModel(application) {

    // Datenbank und Repository initialisieren
    private val database = AppRoomDatabase.Companion.getInstance(application)
    private val dao = database.parkingCardDao()
    private val repository = RoomParkingCardRepository(dao)

    // Den Datenstrom (Flow) für die UI bereitstellen
    val parkingCards = repository.getAllParkingCards()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Companion.WhileSubscribed(5000),
            initialValue = emptyList() // Startet mit einer leeren Liste
        )

    // Funktion zum Hinzufügen einer neuen (Test-)Karte
    fun addDummyCard() {
        viewModelScope.launch {
            val neueKarte = ParkingCard(
                name = "Test-Parkplatz ${System.currentTimeMillis().toString().takeLast(3)}",
                price = 2.50f,
                description = "Ein super Parkplatz direkt im Zentrum.",
                image = "https://images.unsplash.com/photo-1596832323822-b6b383a0967b?q=80&w=2070&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D", // Leer für den Anfang
                latitude = 51.0,
                longitude = 7.0,
                openTime = "08:00",
                closeTime = "18:00",
                isInDiscover = true,
                isInParking = false,
            )
            repository.insert(neueKarte)
        }
    }
}