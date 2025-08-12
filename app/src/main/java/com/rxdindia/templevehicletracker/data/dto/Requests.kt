package com.rxdindia.templevehicletracker.data.dto

// ----- Trip create/update -----
data class TripCreateRequest(
    val startMeterReading: Double? = null,
    val endMeterReading: Double? = null,
    val startTime: String,
    val endTime: String? = null,
    val status: String? = "in_progress",
    val totalDistance: Double? = null,
    val destination: RefDestination? = null,
    val user: RefUser,
    val vehicle: RefVehicle
)

data class TripUpdateRequest(
    val endMeterReading: Double? = null,
    val endTime: String? = null,
    val totalDistance: Double? = null,
    val status: String? = null
)

data class TripResponse(val tripId: Int?)

// ----- Fuel -----
data class FuelLogCreateRequest(
    val fuelDate: String, // yyyy-MM-dd
    val quantity: Double,
    val cost: Double,
    val meterReading: Double? = null,
    val fuelType: String? = null,
    val trip: TripRef? = null,
    val vehicle: RefVehicle
)
data class FuelLogResponse(val fuelId: Int?)

// ----- Maintenance -----
data class MaintenanceLogCreateRequest(
    val maintenanceDate: String, // yyyy-MM-dd
    val description: String,
    val cost: Double,
    val performedBy: String? = null,
    val vehicle: RefVehicle
)
data class MaintenanceLogResponse(val maintenanceId: Int?)

// ----- Trip Detail -----
data class TripDetailCreateRequest(
    val timestamp: String, // yyyy-MM-dd HH:mm:ss
    val latitude: Double,
    val longitude: Double,
    val speed: Double? = null,
    val trip: TripRef
)
data class TripDetailResponse(val detailId: Int?)

// ----- refs that match your Spring entities -----
data class RefUser(val userId: Int)
data class RefVehicle(val vehicleId: Int)
data class RefDestination(val destinationId: Int?)
data class TripRef(val tripId: Int)
