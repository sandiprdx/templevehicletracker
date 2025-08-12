package com.rxdindia.templevehicletracker.sync

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pending_fuel")
data class PendingFuel(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val quantity: Double,
    val cost: Double,
    val vehicleId: Int,
    val tripId: Int? = null
)

@Entity(tableName = "pending_maintenance")
data class PendingMaintenance(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val description: String,
    val cost: Double,
    val vehicleId: Int
)

@Entity(tableName = "pending_trip_detail")
data class PendingTripDetail(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tripId: Int?,   // might be -1 or null for offline-only until server id exists
    val latitude: Double,
    val longitude: Double,
    val speed: Double?
)
