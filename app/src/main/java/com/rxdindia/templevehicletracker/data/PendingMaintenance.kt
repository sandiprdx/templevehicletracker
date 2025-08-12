package com.rxdindia.templevehicletracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pending_maint")
data class PendingMaintenance(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val description: String,
    val cost: Double,
    val vehicleId: Int,
    val createdAt: Long = System.currentTimeMillis()
)
