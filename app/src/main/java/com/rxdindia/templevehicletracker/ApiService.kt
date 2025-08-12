package com.rxdindia.templevehicletracker

import com.rxdindia.templevehicletracker.entity.*
import com.rxdindia.templevehicletracker.viewmodel.LoginResponse
import com.rxdindia.templevehicletracker.network.*
import retrofit2.http.*

interface ApiService {

    // --- Destinations ---
    @GET("api/destinations")
    suspend fun getDestinations(): List<Destination>

    @POST("api/destinations")
    suspend fun addDestination(@Body destination: Destination): Destination

    // --- Vehicles ---
    @GET("api/vehicles")
    suspend fun getVehicles(): List<Vehicle>

    @POST("api/vehicles")
    suspend fun addVehicle(@Body vehicle: Vehicle): Vehicle

    // --- Users / Login ---
    @GET("api/users")
    suspend fun getUsers(): List<User>

    @GET("api/login")
    suspend fun login(
        @Query("username") username: String,
        @Query("password") password: String
    ): LoginResponse

    // --- Trips ---
    @POST("api/trips")
    suspend fun createTrip(@Body body: TripCreateDto): TripResponseDto

    @PUT("api/trips/{tripId}")
    suspend fun updateTrip(
        @Path("tripId") tripId: Int,
        @Body body: TripUpdateDto
    ): TripResponseDto

    @GET("api/trips")
    suspend fun getTrips(): List<Trip>

    // --- Trip Details (breadcrumb points) ---
    @POST("api/trip-details")
    suspend fun addTripDetail(@Body body: TripDetailCreateDto): Unit

    @GET("api/trip-details")
    suspend fun getTripDetails(): List<TripDetail>

    // --- Fuel ---
    @POST("api/fuel-logs")
    suspend fun createFuelLog(@Body body: FuelLogCreateDto): FuelLog

    @GET("api/fuel-logs")
    suspend fun getFuelLogs(): List<FuelLog>

    // --- Maintenance ---
    @POST("api/maintenance-logs")
    suspend fun createMaintenanceLog(@Body body: MaintenanceLogCreateDto): MaintenanceLog

    @GET("api/maintenance-logs")
    suspend fun getMaintenanceLogs(): List<MaintenanceLog>

    // --- Live Location & Admin Poll ---
    @POST("api/locations")
    suspend fun sendLocation(
        @Query("userId") userId: Int,
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double
    ): Boolean

    @GET("api/location-requests/exists")
    suspend fun checkLocationRequestExists(
        @Query("userId") userId: Int,
        @Query("active") active: Boolean = true
    ): Boolean
}
