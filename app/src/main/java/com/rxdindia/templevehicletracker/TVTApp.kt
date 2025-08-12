package com.rxdindia.templevehicletracker

import android.app.Application
import androidx.room.Room
import org.osmdroid.config.Configuration

class TVTApp : Application() {
    companion object {
        lateinit var db: TVTDatabase
            private set
    }

    override fun onCreate() {
        super.onCreate()
        Configuration.getInstance().userAgentValue = "TempleVehicleTracker"
        db = Room.databaseBuilder(
            applicationContext,
            TVTDatabase::class.java,
            "temple_vehicle_db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
}
