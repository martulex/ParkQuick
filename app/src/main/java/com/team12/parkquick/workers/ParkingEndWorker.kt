package com.team12.parkquick.workers

import android.content.Context
import androidx.work.CoroutineWorker // Coroutine Worker statt Worker für Datenbank
import androidx.work.WorkerParameters
import com.team12.parkquick.database.AppRoomDatabase
import com.team12.parkquick.database.RoomParkingCardRepository

class ParkingEndWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val parkingId = inputData.getString("PARKING_ID") ?: return Result.failure()

        val database = AppRoomDatabase.getInstance(applicationContext)
        val dao = database.parkingCardDao()
        val repository = RoomParkingCardRepository(dao)

        try {
            val card = repository.getParkingCardById(parkingId)

            if (card != null) {
                val updatedCard = card.copy(isInParking = false)

                repository.updateParkingCard(updatedCard)
            }

            return Result.success()
        } catch (e: Exception) {
            return Result.failure()
        }
    }
}