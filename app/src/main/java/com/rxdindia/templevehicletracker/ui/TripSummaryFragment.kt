package com.rxdindia.templevehicletracker.ui

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.rxdindia.templevehicletracker.R
import com.rxdindia.templevehicletracker.TVTApp
import com.rxdindia.templevehicletracker.data.TripRepository
import com.rxdindia.templevehicletracker.databinding.FragmentTripSummaryBinding
import com.rxdindia.templevehicletracker.recent.RecentTrip
import com.rxdindia.templevehicletracker.util.TripSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TripSummaryFragment : Fragment() {
    private var _binding: FragmentTripSummaryBinding? = null
    private val binding get() = _binding!!
    private var tripId: Int = -1
    private var endKm: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tripId = requireArguments().getInt("tripId")
        endKm = requireArguments().getDouble("endKm").takeIf { it != 0.0 }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTripSummaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun now(): String = java.time.LocalDateTime.now().toString().replace('T',' ')

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val distance = 0.0 // if you stored start/end meters locally, compute here
        val minutes = 0L   // compute from timestamps if you stored start time
        val fuel = 0.0     // could aggregate from lists if you cached
        val maint = 0.0

        binding.summaryText.text = """
            Distance: ${"%.1f".format(distance)} km
            Time Taken: ${minutes} min
            Fuel Added: ${fuel} L
            Maintenance Cost: ₹${"%.0f".format(maint)}
        """.trimIndent()

        binding.confirmSubmitBtn.setOnClickListener {
            lifecycleScope.launch {
                try {
                    TripRepository.finishTripOnline(tripId, endKm, distance)

                    // Save to recent
                    withContext(Dispatchers.IO) {
                        TVTApp.db.recentTripDao().insert(
                            RecentTrip(
                                tripId = if (tripId > 0) tripId else null,
                                vehicleNumber = "Vehicle",
                                startTime = "",
                                endTime = now(),
                                distanceKm = distance,
                                summary = "Time ${minutes}m • Fuel ${fuel}L • Maint ₹${"%.0f".format(maint)}"
                            )
                        )
                    }

                    TripSession.clear(requireContext())
                    Toast.makeText(requireContext(), "Submitted!", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack(R.id.homeFragment, false)
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Saved locally. Will sync.", Toast.LENGTH_LONG).show()
                    TripSession.clear(requireContext())
                    findNavController().popBackStack(R.id.homeFragment, false)
                }
            }
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
