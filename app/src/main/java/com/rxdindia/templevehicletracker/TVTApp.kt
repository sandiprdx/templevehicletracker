package com.rxdindia.templevehicletracker

import android.app.Application
import androidx.room.Room
import androidx.work.*
import com.rxdindia.templevehicletracker.sync.SyncWorker
import org.osmdroid.config.Configuration
import java.util.concurrent.TimeUnit

class TVTApp : Application() {
    companion object {
        lateinit var db: TVTDatabase
    }

    override fun onCreate() {
        super.onCreate()
        // osmdroid UA
        Configuration.getInstance().userAgentValue = "TempleVehicleTracker"

        db = Room.databaseBuilder(
            applicationContext,
            TVTDatabase::class.java,
            "temple_vehicle_db"
        ).fallbackToDestructiveMigration().build()

        // schedule periodic sync
        val req = PeriodicWorkRequestBuilder<SyncWorker>(15, TimeUnit.MINUTES)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            ).build()
        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork("tvt_sync", ExistingPeriodicWorkPolicy.KEEP, req)
    }
}
