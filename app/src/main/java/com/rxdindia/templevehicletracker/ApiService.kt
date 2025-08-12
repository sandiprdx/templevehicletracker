package com.rxdindia.templevehicletracker

import com.rxdindia.templevehicletracker.data.dto.*
import com.rxdindia.templevehicletracker.entity.*
import retrofit2.http.*

interface ApiService {
    // Reads
    @GET("api/destinations") suspend fun getDestinations(): List<Destination>
    @GET("api/vehicles") suspend fun getVehicles(): List<Vehicle>
    @GET("api/trips") suspend fun getTrips(): List<Trip>
    @GET("api/fuel-logs") suspend fun getFuelLogs(): List<FuelLog>
    @GET("api/maintenance-logs") suspend fun getMaintenanceLogs(): List<MaintenanceLog>

    // Login
    @GET("api/login")
    suspend fun login(
        @Query("username") username: String,
        @Query("password") password: String
    ): com.rxdindia.templevehicletracker.viewmodel.LoginResponse

    // Trip create/update (DTO-shaped to match Spring entities)
    @POST("api/trips")
    suspend fun createTrip(@Body req: TripCreateRequest): TripResponse

    @PUT("api/trips/{id}")
    suspend fun updateTrip(@Path("id") id: Int, @Body req: TripUpdateRequest): TripResponse

    // Fuel/Maint/TripDetail (DTOs)
    @POST("api/fuel-logs")
    suspend fun createFuel(@Body req: FuelLogCreateRequest): FuelLogResponse

    @POST("api/maintenance-logs")
    suspend fun createMaintenance(@Body req: MaintenanceLogCreateRequest): MaintenanceLogResponse

    @POST("api/trip-details")
    suspend fun createTripDetail(@Body req: TripDetailCreateRequest): TripDetailResponse

    // Location ping (optional)
    @POST("api/locations")
    suspend fun sendLocation(
        @Query("userId") userId: Int,
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double
    ): Boolean
}
