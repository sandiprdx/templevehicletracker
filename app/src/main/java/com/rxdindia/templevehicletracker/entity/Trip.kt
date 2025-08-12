package com.rxdindia.templevehicletracker.entity

data class Trip(
    val tripId: Int? = null,
    val userId: Int,
    val vehicleId: Int,
    val startTime: String,
    val endTime: String? = null,
    val totalDistance: Double? = null,
    val status: String? = null,
    val destinationId: Int? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)
