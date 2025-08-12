package com.rxdindia.templevehicletracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.rxdindia.templevehicletracker.recent.RecentTrip
import com.rxdindia.templevehicletracker.recent.RecentTripDao
import com.rxdindia.templevehicletracker.sync.PendingFuel
import com.rxdindia.templevehicletracker.sync.PendingFuelDao
import com.rxdindia.templevehicletracker.sync.PendingMaintenance
import com.rxdindia.templevehicletracker.sync.PendingMaintenanceDao

@Database(
    entities = [
        RecentTrip::class,
        PendingFuel::class,
        PendingMaintenance::class
    ],
    version = 2,
    exportSchema = false
)
abstract class TVTDatabase : RoomDatabase() {
    abstract fun recentTripDao(): RecentTripDao
    abstract fun pendingFuelDao(): PendingFuelDao
    abstract fun pendingMaintenanceDao(): PendingMaintenanceDao
}
