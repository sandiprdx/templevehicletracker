package com.rxdindia.templevehicletracker.entity

data class MaintenanceLog(
    val maintenanceId: Int? = null,
    val vehicleId: Int,
    val maintenanceDate: String,
    val description: String,
    val cost: Double,
    val performedBy: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)
