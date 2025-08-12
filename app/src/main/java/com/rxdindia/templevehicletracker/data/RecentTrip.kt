package com.rxdindia.templevehicletracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recent_trip")
data class RecentTrip(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tripId: Int? = null,
    val vehicleNumber: String,
    val startTime: String,
    val endTime: String,
    val distanceKm: Double,
    val summary: String
)
