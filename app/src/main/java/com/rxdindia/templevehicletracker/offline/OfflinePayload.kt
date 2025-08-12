package com.rxdindia.templevehicletracker.offline

import androidx.room.Entity
import androidx.room.PrimaryKey

// Generic payload table so we can queue Trip / Fuel / Maintenance submissions
@Entity(tableName = "offline_payloads")
data class OfflinePayload(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val endpoint: String,       // e.g., "trip", "fuel", "maintenance"
    val json: String,           // serialized body
    val createdAt: Long = System.currentTimeMillis()
)
