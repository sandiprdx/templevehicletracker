package com.rxdindia.templevehicletracker.recent

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RecentTripDao {
    @Query("SELECT * FROM recent_trip ORDER BY id DESC LIMIT 10")
    suspend fun recent(): List<RecentTrip>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: RecentTrip)
}
