package com.team12.parkquick.database

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ParkingCardDao {

    @Insert
    suspend fun insert (parkingCard : ParkingCard)

    @Query("SELECT * FROM ParkingCard")
    fun getAllParkingCards(): Flow<List<ParkingCard>>

    @Query("SELECT * FROM ParkingCard WHERE id = :parkingCardId")
    suspend fun getParkingCardById(parkingCardId : String) : ParkingCard?

    @Update
    suspend fun updateParkingCard(parkingCard: ParkingCard)

    @Delete
    suspend fun deleteParkingCard(parkingCard: ParkingCard)


}