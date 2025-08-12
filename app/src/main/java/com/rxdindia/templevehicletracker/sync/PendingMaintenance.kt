package com.rxdindia.templevehicletracker.sync

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pending_maintenance")
data class PendingMaintenance(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val vehicleId: Int,
    val maintenanceDate: String,   // "yyyy-MM-dd HH:mm:ss"
    val description: String,
    val cost: Double
)
