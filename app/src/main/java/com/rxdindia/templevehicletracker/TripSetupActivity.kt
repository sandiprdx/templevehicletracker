package com.rxdindia.templevehicletracker

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.rxdindia.templevehicletracker.databinding.ActivityTripSetupBinding
import com.rxdindia.templevehicletracker.entity.Trip
import com.rxdindia.templevehicletracker.viewmodel.TripViewModel
import android.widget.Toast

class TripSetupActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTripSetupBinding
    private val tripViewModel: TripViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTripSetupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.startTripButton.setOnClickListener {
            val trip = Trip(userId = 1, vehicleId = 1, startTime = "2025-07-29 12:30")
            tripViewModel.startTrip(trip)
        }

        tripViewModel.startTripResult.observe(this) { result ->
            result?.fold(
                onSuccess = {
                    Toast.makeText(this, "Trip started: ${it.tripId}", Toast.LENGTH_SHORT).show()
                    finish()
                },
                onFailure = {
                    Toast.makeText(this, "Failed to start trip: ${it.message}", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}
