package com.rxdindia.templevehicletracker.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.rxdindia.templevehicletracker.Network
import com.rxdindia.templevehicletracker.TVTApp
import com.rxdindia.templevehicletracker.entity.FuelLog
import com.rxdindia.templevehicletracker.entity.MaintenanceLog

class SyncWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        val db = TVTApp.db

        // fuel
        val fuels = db.pendingFuelDao().all()
        for (pf in fuels) {
            try {
                Network.apiService.createFuelLog(
                    FuelLog(
                        vehicleId = pf.vehicleId,
                        fuelDate = pf.fuelDate,
                        quantity = pf.quantity,
                        cost = pf.cost,
                        tripId = pf.tripId
                    )
                )
                db.pendingFuelDao().delete(pf)
            } catch (_: Exception) {
                // keep it; will retry next time
            }
        }

        // maintenance
        val ms = db.pendingMaintenanceDao().all()
        for (pm in ms) {
            try {
                Network.apiService.createMaintenanceLog(
                    MaintenanceLog(
                        vehicleId = pm.vehicleId,
                        maintenanceDate = pm.maintenanceDate,
                        description = pm.description,
                        cost = pm.cost
                    )
                )
                db.pendingMaintenanceDao().delete(pm)
            } catch (_: Exception) {
                // keep it
            }
        }
        return Result.success()
    }
}
