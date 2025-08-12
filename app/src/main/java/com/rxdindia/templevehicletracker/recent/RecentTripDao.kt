package com.rxdindia.templevehicletracker.recent

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RecentTripDao {
    @Insert suspend fun insert(item: RecentTrip)
    @Query("SELECT * FROM recent_trips ORDER BY id DESC LIMIT 10")
    suspend fun latest(): List<RecentTrip>
}
