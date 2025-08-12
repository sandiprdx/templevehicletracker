package com.rxdindia.templevehicletracker.entity

data class Vehicle(
    val vehicleId: Int,
    val vehicleNumber: String,
    val vehicleType: String?,
    val fuelType: String?,
    val status: String?,
    val lastMaintenanceDate: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)
