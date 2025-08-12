package com.rxdindia.templevehicletracker.entity

data class FuelLog(
    val fuelId: Int? = null,
    val vehicleId: Int,
    val fuelDate: String,
    val quantity: Double,
    val cost: Double,
    val meterReading: Double? = null,
    val fuelType: String? = null,
    val tripId: Int? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)
