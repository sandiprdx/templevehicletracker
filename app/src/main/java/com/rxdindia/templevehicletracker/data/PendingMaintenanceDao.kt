package com.rxdindia.templevehicletracker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PendingMaintenanceDao {
    @Insert suspend fun insert(item: PendingMaintenance)
    @Query("SELECT * FROM pending_maint ORDER BY createdAt ASC") suspend fun all(): List<PendingMaintenance>
    @Query("DELETE FROM pending_maint WHERE id IN (:ids)") suspend fun deleteByIds(ids: List<Long>)
}
