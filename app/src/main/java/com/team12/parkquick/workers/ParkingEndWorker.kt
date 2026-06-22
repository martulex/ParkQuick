package com.team12.parkquick.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.team12.parkquick.viewmodels.ParkingViewModel

class ParkingEndWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {
        val parkingId = inputData.getString("PARKING_ID") ?: return Result.failure()

        // Da die App im Hintergrund laufen oder geschlossen sein kann,
        // greifen wir auf eine statische/zentrale Instanz oder Datenbank zu.
        // Stand jetzt (In-Memory Liste): Wir setzen den Status um.
        ParkingViewModel.setParkingExpired(parkingId)

        return Result.success()
    }
}