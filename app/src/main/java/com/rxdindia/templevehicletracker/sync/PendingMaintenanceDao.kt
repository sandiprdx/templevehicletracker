package com.rxdindia.templevehicletracker.sync

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PendingMaintenanceDao {
    @Insert suspend fun insert(item: PendingMaintenance)
    @Query("SELECT * FROM pending_maint")
    suspend fun all(): List<PendingMaintenance>
    @Delete suspend fun delete(item: PendingMaintenance)
}
