package com.rxdindia.templevehicletracker.data

import com.rxdindia.templevehicletracker.Network
import com.rxdindia.templevehicletracker.SessionManager
import com.rxdindia.templevehicletracker.entity.FuelLog
import com.rxdindia.templevehicletracker.entity.MaintenanceLog
import com.rxdindia.templevehicletracker.network.*
import com.rxdindia.templevehicletracker.sync.PendingFuel
import com.rxdindia.templevehicletracker.sync.PendingMaintenance
import com.rxdindia.templevehicletracker.TVTApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object TripRepository {

    private val dateFmt: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    suspend fun startTripOnlineOrQueue(
        vehicleId: Int,
        destinationId: Int?,
        startMeter: Double?
    ): Int /* tripId or -1 if queued */ = withContext(Dispatchers.IO) {
        val userId = SessionManager.getUserId(TVTApp.db.context)
        try {
            val created = Network.apiService.createTrip(
                TripCreateDto(
                    user = UserRef(userId),
                    vehicle = VehicleRef(vehicleId),
                    destination = destinationId?.let { DestinationRef(it) },
                    startMeterReading = startMeter
                )
            )
            created.tripId ?: -1
        } catch (_: Exception) {
            // offline: queue nothing for start (we’ll create on submit page if still offline)
            -1
        }
    }

    suspend fun finishTripOnlineOrQueue(
        tripId: Int,
        vehicleId: Int,
        userId: Int,
        endMeter: Double?,
        totalDistance: Double?,
        markCompleted: Boolean
    ) = withContext(Dispatchers.IO) {
        val status = if (markCompleted) "completed" else null
        try {
            if (tripId > 0) {
                Network.apiService.updateTrip(
                    tripId,
                    TripUpdateDto(
                        user = UserRef(userId),
                        vehicle = VehicleRef(vehicleId),
                        endMeterReading = endMeter,
                        totalDistance = totalDistance,
                        status = status
                        // endTime omitted -> stays null; add if you want to set now()
                    )
                )
            } else {
                // No server trip id. You could create a final Trip now or just rely on RecentTrip.
            }
        } catch (_: Exception) {
            // queue a local “pending finish” if you want (not shown here)
        }
    }

    suspend fun addFuelOnlineOrQueue(
        tripId: Int?,
        vehicleId: Int,
        quantity: Double,
        cost: Double,
        meterReading: Double,
        fuelType: String? = null
    ): FuelLog = withContext(Dispatchers.IO) {
        val body = FuelLogCreateDto(
            vehicle = VehicleRef(vehicleId),
            trip = tripId?.let { TripRef(it) },
            fuelDate = LocalDate.now().format(dateFmt),
            quantity = quantity,
            cost = cost,
            meterReading = meterReading,
            fuelType = fuelType
        )
        try {
            Network.apiService.createFuelLog(body)
        } catch (_: Exception) {
            TVTApp.db.pendingFuelDao().insert(
                PendingFuel(
                    vehicleId = vehicleId,
                    tripId = tripId,
                    fuelDate = body.fuelDate,
                    quantity = quantity,
                    cost = cost,
                    meterReading = meterReading,
                    fuelType = fuelType
                )
            )
            // return a fake object for UI
            FuelLog(
                fuelId = null,
                vehicleId = vehicleId,
                fuelDate = body.fuelDate,
                quantity = quantity,
                cost = cost,
                meterReading = meterReading,
                fuelType = fuelType,
                tripId = tripId
            )
        }
    }

    suspend fun addMaintenanceOnlineOrQueue(
        vehicleId: Int,
        description: String,
        cost: Double
    ): MaintenanceLog = withContext(Dispatchers.IO) {
        val body = MaintenanceLogCreateDto(
            vehicle = VehicleRef(vehicleId),
            maintenanceDate = LocalDate.now().format(dateFmt),
            description = description,
            cost = cost
        )
        try {
            Network.apiService.createMaintenanceLog(body)
        } catch (_: Exception) {
            TVTApp.db.pendingMaintenanceDao().insert(
                PendingMaintenance(
                    vehicleId = vehicleId,
                    maintenanceDate = body.maintenanceDate,
                    description = description,
                    cost = cost
                )
            )
            MaintenanceLog(
                maintenanceId = null,
                vehicleId = vehicleId,
                maintenanceDate = body.maintenanceDate,
                description = description,
                cost = cost
            )
        }
    }

    suspend fun addTripPointIfOnline(tripId: Int, lat: Double, lon: Double, speed: Double?) =
        withContext(Dispatchers.IO) {
            try {
                Network.apiService.addTripDetail(
                    TripDetailCreateDto(
                        trip = TripRef(tripId),
                        latitude = lat,
                        longitude = lon,
                        speed = speed
                    )
                )
            } catch (_: Exception) {
                // If you want, create a PendingTripDetail table and queue (not implemented here)
            }
        }
}
