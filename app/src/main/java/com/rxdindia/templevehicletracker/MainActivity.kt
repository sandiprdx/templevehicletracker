package com.rxdindia.templevehicletracker

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navController = findNavController(R.id.nav_host_fragment)
        val bottom = findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottom.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, dest, _ ->
            val hide = dest.id in setOf(
                R.id.tripSetupFragment, R.id.tripTrackingFragment, R.id.endTripFragment, R.id.tripSummaryFragment
            )
            bottom.visibility = if (hide) View.GONE else View.VISIBLE
        }
    }
}
