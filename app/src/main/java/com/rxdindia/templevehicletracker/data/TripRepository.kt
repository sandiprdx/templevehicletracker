package com.rxdindia.templevehicletracker.data

import android.content.Context
import com.rxdindia.templevehicletracker.Network
import com.rxdindia.templevehicletracker.SessionManager
import com.rxdindia.templevehicletracker.TVTApp
import com.rxdindia.templevehicletracker.data.dto.*
import com.rxdindia.templevehicletracker.sync.PendingFuel
import com.rxdindia.templevehicletracker.sync.PendingMaintenance
import com.rxdindia.templevehicletracker.sync.PendingTripDetail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object TripRepository {

    suspend fun startTripOnlineOrQueue(
        context: Context,
        vehicleId: Int,
        destinationId: Int?,
        startMeter: Double?,
        startTime: String
    ): Int = withContext(Dispatchers.IO) {
        val userId = SessionManager.getUserId(context).takeIf { it > 0 }
            ?: throw IllegalStateException("No logged in user")

        return@withContext try {
            val res = Network.apiService.createTrip(
                TripCreateRequest(
                    startMeterReading = startMeter,
                    startTime = startTime,
                    status = "in_progress",
                    destination = destinationId?.let { RefDestination(it) },
                    user = RefUser(userId),
                    vehicle = RefVehicle(vehicleId)
                )
            )
            res.tripId ?: -1
        } catch (_: Exception) {
            -1
        }
    }

    suspend fun addFuelOnlineOrQueue(
        context: Context,
        quantity: Double,
        cost: Double,
        vehicleId: Int,
        tripId: Int?
    ) = withContext(Dispatchers.IO) {
        try {
            Network.apiService.createFuel(
                FuelLogCreateRequest(
                    fuelDate = nowDate(),
                    quantity = quantity,
                    cost = cost,
                    vehicle = RefVehicle(vehicleId),
                    trip = tripId?.takeIf { it > 0 }?.let { TripRef(it) }
                )
            )
        } catch (_: Exception) {
            TVTApp.db.pendingFuelDao().insert(
                PendingFuel(quantity = quantity, cost = cost, vehicleId = vehicleId, tripId = tripId)
            )
        }
    }

    suspend fun addMaintenanceOnlineOrQueue(
        context: Context,
        description: String,
        cost: Double,
        vehicleId: Int
    ) = withContext(Dispatchers.IO) {
        try {
            Network.apiService.createMaintenance(
                MaintenanceLogCreateRequest(
                    maintenanceDate = nowDate(),
                    description = description,
                    cost = cost,
                    vehicle = RefVehicle(vehicleId)
                )
            )
        } catch (_: Exception) {
            TVTApp.db.pendingMaintenanceDao().insert(
                PendingMaintenance(description = description, cost = cost, vehicleId = vehicleId)
            )
        }
    }

    suspend fun pushTripPointOnlineOrQueue(
        context: Context,
        tripId: Int?,
        lat: Double,
        lon: Double,
        speed: Double?
    ) = withContext(Dispatchers.IO) {
        if (tripId != null && tripId > 0) {
            try {
                Network.apiService.createTripDetail(
                    TripDetailCreateRequest(
                        timestamp = nowDateTime(),
                        latitude = lat,
                        longitude = lon,
                        speed = speed,
                        trip = TripRef(tripId)
                    )
                )
            } catch (_: Exception) {
                TVTApp.db.pendingTripDetailDao().insert(
                    PendingTripDetail(tripId = tripId, latitude = lat, longitude = lon, speed = speed)
                )
            }
        } else {
            TVTApp.db.pendingTripDetailDao().insert(
                PendingTripDetail(tripId = -1, latitude = lat, longitude = lon, speed = speed)
            )
        }
    }

    suspend fun finishTripOnline(
        tripId: Int,
        endMeter: Double?,
        totalDistance: Double?
    ) = withContext(Dispatchers.IO) {
        if (tripId > 0) {
            try {
                Network.apiService.updateTrip(
                    tripId,
                    TripUpdateRequest(
                        endMeterReading = endMeter,
                        endTime = nowDateTime(),
                        totalDistance = totalDistance,
                        status = "completed"
                    )
                )
            } catch (_: Exception) {
                // optional: queue a pending trip update table
            }
        }
    }

    private fun nowDate(): String = java.time.LocalDate.now().toString()
    private fun nowDateTime(): String = java.time.LocalDateTime.now().toString().replace('T', ' ')
}
