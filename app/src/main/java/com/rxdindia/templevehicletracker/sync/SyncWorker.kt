package com.rxdindia.templevehicletracker.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.rxdindia.templevehicletracker.Network
import com.rxdindia.templevehicletracker.TVTApp
import com.rxdindia.templevehicletracker.data.dto.*

class SyncWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        val db = TVTApp.db

        // Fuels
        db.pendingFuelDao().all().forEach { pf ->
            try {
                Network.apiService.createFuel(
                    FuelLogCreateRequest(
                        fuelDate = nowDate(),
                        quantity = pf.quantity,
                        cost = pf.cost,
                        meterReading = null,
                        fuelType = null,
                        trip = pf.tripId?.takeIf { it > 0 }?.let { TripRef(it) },
                        vehicle = RefVehicle(pf.vehicleId)
                    )
                )
                db.pendingFuelDao().delete(pf)
            } catch (_: Exception) { }
        }

        // Maintenance
        db.pendingMaintenanceDao().all().forEach { pm ->
            try {
                Network.apiService.createMaintenance(
                    MaintenanceLogCreateRequest(
                        maintenanceDate = nowDate(),
                        description = pm.description,
                        cost = pm.cost,
                        performedBy = null,
                        vehicle = RefVehicle(pm.vehicleId)
                    )
                )
                db.pendingMaintenanceDao().delete(pm)
            } catch (_: Exception) { }
        }

        // Trip details
        db.pendingTripDetailDao().all().forEach { pt ->
            try {
                pt.tripId?.takeIf { it > 0 }?.let { tid ->
                    Network.apiService.createTripDetail(
                        TripDetailCreateRequest(
                            timestamp = nowDateTime(),
                            latitude = pt.latitude,
                            longitude = pt.longitude,
                            speed = pt.speed,
                            trip = TripRef(tid)
                        )
                    )
                    db.pendingTripDetailDao().delete(pt)
                }
            } catch (_: Exception) { }
        }

        return Result.success()
    }
}

private fun nowDate(): String = java.time.LocalDate.now().toString()
private fun nowDateTime(): String = java.time.LocalDateTime.now().toString().replace('T', ' ')
