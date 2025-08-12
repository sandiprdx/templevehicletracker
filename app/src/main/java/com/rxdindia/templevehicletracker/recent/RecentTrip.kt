package com.rxdindia.templevehicletracker.recent

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recent_trips")
data class RecentTrip(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tripId: Int?,
    val vehicleNumber: String,
    val startTime: String,
    val endTime: String,
    val distanceKm: Double,
    val summary: String
)
