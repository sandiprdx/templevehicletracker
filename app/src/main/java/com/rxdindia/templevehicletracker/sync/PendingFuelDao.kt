package com.rxdindia.templevehicletracker.sync

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PendingFuelDao {
    @Insert suspend fun insert(item: PendingFuel)
    @Query("SELECT * FROM pending_fuel")
    suspend fun all(): List<PendingFuel>
    @Delete suspend fun delete(item: PendingFuel)
}
