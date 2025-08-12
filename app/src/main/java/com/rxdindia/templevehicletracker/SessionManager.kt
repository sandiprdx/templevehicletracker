package com.rxdindia.templevehicletracker

import android.content.Context

object SessionManager {
    private const val PREF = "tvt_prefs"
    private const val KEY_LOGGED_IN = "is_logged_in"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_ROLE = "role"

    fun saveLogin(context: Context, userId: Int, role: String?) {
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE).edit()
            .putBoolean(KEY_LOGGED_IN, true)
            .putInt(KEY_USER_ID, userId)
            .putString(KEY_ROLE, role)
            .apply()
    }

    fun isLoggedIn(context: Context) =
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE).getBoolean(KEY_LOGGED_IN, false)

    fun getUserId(context: Context) =
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE).getInt(KEY_USER_ID, -1)

    fun clear(context: Context) =
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE).edit().clear().apply()
}
