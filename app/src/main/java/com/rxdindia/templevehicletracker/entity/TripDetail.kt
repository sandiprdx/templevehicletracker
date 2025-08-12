package com.rxdindia.templevehicletracker.entity

data class TripDetail(
    val detailId: Int,
    val tripId: Int,
    val timestamp: String,
    val latitude: Double,
    val longitude: Double,
    val meterReading: Double?,
    val speed: Double?,
    val createdAt: String? = null
)
