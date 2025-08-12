package com.rxdindia.templevehicletracker.sync

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pending_trip_detail")
data class PendingTripDetail(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tripId: Int?,               // may be null while offline
    val timestamp: String,          // "yyyy-MM-dd HH:mm:ss"
    val latitude: Double,
    val longitude: Double,
    val meterReading: Double? = null,
    val speed: Double? = null
)
