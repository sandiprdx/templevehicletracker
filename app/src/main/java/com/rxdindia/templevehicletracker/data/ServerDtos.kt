package com.rxdindia.templevehicletracker.data

// Minimal nested references so Spring @ManyToOne works
data class RefUser(val userId: Int)
data class RefVehicle(val vehicleId: Int)
data class RefDestination(val destinationId: Int?)

// Trip create/update
data class TripCreateRequest(
    val startMeterReading: Double? = null,
    val startTime: String,
    val status: String = "in_progress",
    val user: RefUser,
    val vehicle: RefVehicle,
    val destination: RefDestination?
)
data class TripUpdateRequest(
    val endMeterReading: Double? = null,
    val endTime: String? = null,
    val totalDistance: Double? = null,
    val status: String? = null
)

// Trip response subset
data class TripResponse(
    val tripId: Int?,
    val startTime: String?,
    val endTime: String?,
    val totalDistance: Double?
)

// Trip detail (GPS points)
data class TripDetailCreateRequest(
    val timestamp: String,
    val latitude: Double,
    val longitude: Double,
    val meterReading: Double? = null,
    val speed: Double? = null,
    val trip: TripRef
)
data class TripRef(val tripId: Int)

// Fuel & maintenance
data class FuelLogCreateRequest(
    val fuelDate: String,
    val quantity: Double,
    val cost: Double,
    val meterReading: Double? = null,
    val fuelType: String? = null,
    val trip: TripRef? = null,           // optional
    val vehicle: RefVehicle
)
data class MaintenanceLogCreateRequest(
    val maintenanceDate: String,
    val description: String,
    val cost: Double,
    val performedBy: String? = null,
    val vehicle: RefVehicle
)
