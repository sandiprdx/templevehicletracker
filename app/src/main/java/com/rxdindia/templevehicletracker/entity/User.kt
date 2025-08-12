package com.rxdindia.templevehicletracker.entity

data class User(
    val userId: Int,
    val username: String,
    val fullName: String,
    val email: String?,
    val role: String,
    val createdAt: String? = null,
    val updatedAt: String? = null
)
