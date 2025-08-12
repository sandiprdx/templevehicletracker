package com.rxdindia.templevehicletracker.util

import android.content.Context
import androidx.core.content.edit

object TripSession {
    private const val SP = "trip_session"

    private const val KEY_ACTIVE_TRIP_ID = "active_trip_id"
    private const val KEY_VEHICLE_ID = "vehicle_id"
    private const val KEY_VEHICLE_NUM = "vehicle_num"

    private const val KEY_START_KM = "start_km"
    private const val KEY_START_PHOTO = "start_photo"
    private const val KEY_DEST_ID = "dest_id"
    private const val KEY_DEST_CUSTOM = "dest_custom"
    private const val KEY_START_TIME = "start_time"

    private const val KEY_FUEL_LITERS = "fuel_liters_sum"
    private const val KEY_FUEL_COST = "fuel_cost_sum"
    private const val KEY_MAINT_COST = "maint_cost_sum"

    fun setActive(
        context: Context,
        tripId: Int,
        vehicleId: Int,
        vehicleNumber: String?,
        startKm: Double?,
        startPhotoPath: String?,
        destId: Int?,
        destCustom: String?,
        startTime: String
    ) {
        val sp = context.getSharedPreferences(SP, Context.MODE_PRIVATE)
        sp.edit {
            putInt(KEY_ACTIVE_TRIP_ID, tripId)
            putInt(KEY_VEHICLE_ID, vehicleId)
            putString(KEY_VEHICLE_NUM, vehicleNumber)
            if (startKm != null) putString(KEY_START_KM, startKm.toString()) else remove(KEY_START_KM)
            putString(KEY_START_PHOTO, startPhotoPath)
            if (destId != null) putInt(KEY_DEST_ID, destId) else remove(KEY_DEST_ID)
            putString(KEY_DEST_CUSTOM, destCustom)
            putString(KEY_START_TIME, startTime)
            putString(KEY_FUEL_LITERS, "0.0")
            putString(KEY_FUEL_COST, "0.0")
            putString(KEY_MAINT_COST, "0.0")
        }
    }

    fun addFuel(context: Context, liters: Double, cost: Double) {
        val sp = context.getSharedPreferences(SP, Context.MODE_PRIVATE)
        val L = (sp.getString(KEY_FUEL_LITERS, "0.0") ?: "0.0").toDoubleOrNull() ?: 0.0
        val C = (sp.getString(KEY_FUEL_COST, "0.0") ?: "0.0").toDoubleOrNull() ?: 0.0
        sp.edit {
            putString(KEY_FUEL_LITERS, (L + liters).toString())
            putString(KEY_FUEL_COST, (C + cost).toString())
        }
    }

    fun addMaintenance(context: Context, cost: Double) {
        val sp = context.getSharedPreferences(SP, Context.MODE_PRIVATE)
        val C = (sp.getString(KEY_MAINT_COST, "0.0") ?: "0.0").toDoubleOrNull() ?: 0.0
        sp.edit { putString(KEY_MAINT_COST, (C + cost).toString()) }
    }

    fun fuelLiters(context: Context): Double =
        (context.getSharedPreferences(SP, Context.MODE_PRIVATE).getString(KEY_FUEL_LITERS, "0.0") ?: "0.0").toDoubleOrNull() ?: 0.0

    fun fuelCost(context: Context): Double =
        (context.getSharedPreferences(SP, Context.MODE_PRIVATE).getString(KEY_FUEL_COST, "0.0") ?: "0.0").toDoubleOrNull() ?: 0.0

    fun maintenanceCost(context: Context): Double =
        (context.getSharedPreferences(SP, Context.MODE_PRIVATE).getString(KEY_MAINT_COST, "0.0") ?: "0.0").toDoubleOrNull() ?: 0.0

    fun clear(context: Context) =
        context.getSharedPreferences(SP, Context.MODE_PRIVATE).edit { clear() }

    fun activeTripId(context: Context): Int? =
        context.getSharedPreferences(SP, Context.MODE_PRIVATE)
            .getInt(KEY_ACTIVE_TRIP_ID, -1).let { if (it > 0) it else null }

    fun vehicleId(context: Context): Int =
        context.getSharedPreferences(SP, Context.MODE_PRIVATE).getInt(KEY_VEHICLE_ID, 0)

    fun vehicleNumber(context: Context): String =
        context.getSharedPreferences(SP, Context.MODE_PRIVATE).getString(KEY_VEHICLE_NUM, "Vehicle") ?: "Vehicle"

    fun startKm(context: Context): Double? =
        context.getSharedPreferences(SP, Context.MODE_PRIVATE)
            .getString(KEY_START_KM, null)?.toDoubleOrNull()

    fun startPhoto(context: Context): String? =
        context.getSharedPreferences(SP, Context.MODE_PRIVATE)
            .getString(KEY_START_PHOTO, null)

    fun startTime(context: Context): String? =
        context.getSharedPreferences(SP, Context.MODE_PRIVATE)
            .getString(KEY_START_TIME, null)
}
