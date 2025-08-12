package com.rxdindia.templevehicletracker.sync

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pending_fuel")
data class PendingFuel(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val vehicleId: Int,
    val fuelDate: String,
    val quantity: Double,
    val cost: Double,
    val tripId: Int?
)
