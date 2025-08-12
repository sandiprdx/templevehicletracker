package com.rxdindia.templevehicletracker.util

import android.content.Context

object TripSession {
    private const val PREF = "tvt_trip_session"
    private const val K_TRIP_ID = "trip_id"
    private const val K_VEHICLE_ID = "vehicle_id"
    private const val K_START_KM = "start_km"
    private const val K_START_PHOTO = "start_photo"
    private const val K_DEST_ID = "dest_id"
    private const val K_DEST_CUSTOM = "dest_custom"
    private const val K_START_TIME = "start_time"

    fun setActive(
        ctx: Context,
        tripId: Int,
        vehicleId: Int,
        startKm: Double?,
        startPhoto: String?,
        destId: Int?,
        destCustom: String?,
        startTime: String
    ) {
        val sp = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE).edit()
        sp.putInt(K_TRIP_ID, tripId)
        sp.putInt(K_VEHICLE_ID, vehicleId)
        sp.putString(K_START_TIME, startTime)
        if (startKm != null) sp.putFloat(K_START_KM, startKm.toFloat()) else sp.remove(K_START_KM)
        if (startPhoto != null) sp.putString(K_START_PHOTO, startPhoto) else sp.remove(K_START_PHOTO)
        if (destId != null) sp.putInt(K_DEST_ID, destId) else sp.remove(K_DEST_ID)
        if (!destCustom.isNullOrBlank()) sp.putString(K_DEST_CUSTOM, destCustom) else sp.remove(K_DEST_CUSTOM)
        sp.apply()
    }

    fun clear(ctx: Context) {
        ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE).edit().clear().apply()
    }

    fun activeTripId(ctx: Context): Int? =
        ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE).getInt(K_TRIP_ID, -999).let {
            if (it == -999) null else it
        }

    fun vehicleId(ctx: Context): Int? =
        ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE).getInt(K_VEHICLE_ID, -999).let {
            if (it == -999) null else it
        }
}
