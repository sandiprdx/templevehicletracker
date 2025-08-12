package com.rxdindia.templevehicletracker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PendingTripDetailDao {
    @Insert suspend fun insert(item: PendingTripDetail)
    @Query("SELECT * FROM pending_trip_detail ORDER BY createdAt ASC") suspend fun all(): List<PendingTripDetail>
    @Query("DELETE FROM pending_trip_detail WHERE id IN (:ids)") suspend fun deleteByIds(ids: List<Long>)
}
