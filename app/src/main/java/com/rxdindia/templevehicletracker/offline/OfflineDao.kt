package com.rxdindia.templevehicletracker.offline

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface OfflineDao {
    @Insert suspend fun insert(item: OfflinePayload): Long
    @Query("SELECT * FROM offline_payloads ORDER BY createdAt ASC")
    suspend fun getAll(): List<OfflinePayload>
    @Query("DELETE FROM offline_payloads WHERE id = :id")
    suspend fun deleteById(id: Int)
}
