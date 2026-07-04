package com.team12.parkquick.database

import kotlinx.coroutines.flow.Flow

class RoomParkingCardRepository (private val parkingCardDao: ParkingCardDao)  {

    suspend fun insert(parkingCard: ParkingCard) {
        parkingCardDao.insert(parkingCard)
    }

    fun getAllParkingCards(): Flow<List<ParkingCard>> {
        return parkingCardDao.getAllParkingCards()
    }

    suspend fun getParkingCardById(parkingCardId: String): ParkingCard? {
        return parkingCardDao.getParkingCardById(parkingCardId)
    }

    suspend fun updateParkingCard(parkingCard: ParkingCard) {

        parkingCardDao.updateParkingCard(parkingCard)

    }

    suspend fun deleteParkingCard(parkingCard: ParkingCard) {

        parkingCardDao.deleteParkingCard(parkingCard)

    }
}