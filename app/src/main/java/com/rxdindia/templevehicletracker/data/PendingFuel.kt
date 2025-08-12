package com.rxdindia.templevehicletracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pending_fuel")
data class PendingFuel(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val quantity: Double,
    val cost: Double,
    val vehicleId: Int,
    val tripId: Int? = null,
    val createdAt: Long = System.currentTimeMillis()
)
