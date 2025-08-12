package com.rxdindia.templevehicletracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pending_trip_detail")
data class PendingTripDetail(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tripId: Int,
    val latitude: Double,
    val longitude: Double,
    val speed: Double? = null,
    val createdAt: Long = System.currentTimeMillis()
)
