package com.rxdindia.templevehicletracker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PendingFuelDao {
    @Insert suspend fun insert(item: PendingFuel)
    @Query("SELECT * FROM pending_fuel ORDER BY createdAt ASC") suspend fun all(): List<PendingFuel>
    @Query("DELETE FROM pending_fuel WHERE id IN (:ids)") suspend fun deleteByIds(ids: List<Long>)
}
