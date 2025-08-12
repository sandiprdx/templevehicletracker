package com.rxdindia.templevehicletracker.network

// --- Minimal ID refs for JPA @ManyToOne nesting ---
data class UserRef(val userId: Int)
data class VehicleRef(val vehicleId: Int)
data class DestinationRef(val destinationId: Int)
data class TripRef(val tripId: Int)

// --- Trip: create & update & response ---
data class TripCreateDto(
    val user: UserRef,
    val vehicle: VehicleRef,
    val destination: DestinationRef? = null,
    val startMeterReading: Double? = null,
    // omit startTime/createdAt/updatedAt -> server fills
    val status: String? = null // optional, server defaults "planned"
)

data class TripUpdateDto(
    val user: UserRef,                // non-null on server
    val vehicle: VehicleRef,          // non-null on server
    val destination: DestinationRef? = null,
    val endMeterReading: Double? = null,
    val totalDistance: Double? = null,
    val status: String? = null,       // e.g., "completed"
    val endTime: String? = null       // optional; if null, stays null
)

data class TripResponseDto(
    val tripId: Int?,
    val user: UserRef?,
    val vehicle: VehicleRef?,
    val destination: DestinationRef?
    // other server fields exist but we don't need them on client right now
)

// --- Trip Detail (breadcrumb point) ---
data class TripDetailCreateDto(
    val trip: TripRef,
    val latitude: Double,
    val longitude: Double,
    val speed: Double? = null
    // omit timestamp/createdAt -> server default "now"
)

// --- Fuel & Maintenance ---
data class FuelLogCreateDto(
    val vehicle: VehicleRef,
    val trip: TripRef? = null,                 // optional in DB
    val fuelDate: String,                      // "YYYY-MM-DD"
    val quantity: Double,
    val cost: Double,
    val meterReading: Double,                  // REQUIRED by server
    val fuelType: String? = null               // optional, server default "petrol"
)

data class MaintenanceLogCreateDto(
    val vehicle: VehicleRef,
    val maintenanceDate: String,               // "YYYY-MM-DD"
    val description: String,
    val cost: Double,
    val performedBy: String? = null
)
