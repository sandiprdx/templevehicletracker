package com.rxdindia.templevehicletracker.sync

import androidx.room.*

@Dao
interface PendingFuelDao {
    @Query("SELECT * FROM pending_fuel ORDER BY id ASC")
    suspend fun all(): List<PendingFuel>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: PendingFuel)
    @Delete
    suspend fun delete(entity: PendingFuel)
}

@Dao
interface PendingMaintenanceDao {
    @Query("SELECT * FROM pending_maintenance ORDER BY id ASC")
    suspend fun all(): List<PendingMaintenance>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: PendingMaintenance)
    @Delete
    suspend fun delete(entity: PendingMaintenance)
}

@Dao
interface PendingTripDetailDao {
    @Query("SELECT * FROM pending_trip_detail ORDER BY id ASC")
    suspend fun all(): List<PendingTripDetail>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: PendingTripDetail)
    @Delete
    suspend fun delete(entity: PendingTripDetail)
}
