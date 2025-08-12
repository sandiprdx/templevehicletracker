package com.rxdindia.templevehicletracker

import androidx.room.Database
import androidx.room.RoomDatabase
import com.rxdindia.templevehicletracker.recent.RecentTrip
import com.rxdindia.templevehicletracker.recent.RecentTripDao
import com.rxdindia.templevehicletracker.sync.PendingFuel
import com.rxdindia.templevehicletracker.sync.PendingFuelDao
import com.rxdindia.templevehicletracker.sync.PendingMaintenance
import com.rxdindia.templevehicletracker.sync.PendingMaintenanceDao
import com.rxdindia.templevehicletracker.sync.PendingTripDetail
import com.rxdindia.templevehicletracker.sync.PendingTripDetailDao

const val TVT_DB_VERSION: Int = 6  // bump when schema changes

@Database(
    entities = [
        PendingFuel::class,
        PendingMaintenance::class,
        PendingTripDetail::class,
        RecentTrip::class
    ],
    version = TVT_DB_VERSION,
    exportSchema = false // set false to silence schema export error; or see Gradle args below
)
abstract class TVTDatabase : RoomDatabase() {
    abstract fun pendingFuelDao(): PendingFuelDao
    abstract fun pendingMaintenanceDao(): PendingMaintenanceDao
    abstract fun pendingTripDetailDao(): PendingTripDetailDao
    abstract fun recentTripDao(): RecentTripDao
}
