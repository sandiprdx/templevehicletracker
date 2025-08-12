package com.rxdindia.templevehicletracker.entity

data class Destination(
    val destinationId: Int,
    val name: String,
    val address: String? = null,
    val createdAt: String? = null
)
