package com.rxdindia.templevehicletracker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RecentTripDao {
    @Insert suspend fun insert(item: RecentTrip)
    @Query("SELECT * FROM recent_trip ORDER BY id DESC LIMIT 10") suspend fun recent(): List<RecentTrip>
    @Query("DELETE FROM recent_trip") suspend fun clearAll()
}
