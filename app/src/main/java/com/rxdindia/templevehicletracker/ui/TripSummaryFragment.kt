package com.rxdindia.templevehicletracker.ui

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.rxdindia.templevehicletracker.R
import com.rxdindia.templevehicletracker.databinding.FragmentTripSummaryBinding

class TripSummaryFragment : Fragment() {
    private var _binding: FragmentTripSummaryBinding? = null
    private val binding get() = _binding!!
    private var tripId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tripId = requireArguments().getInt("tripId")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTripSummaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val distance = arguments?.getDouble("distanceKm") ?: 0.0
        val minutes = arguments?.getLong("durationMin") ?: 0L

        binding.summaryText.text = """
            Distance: ${"%.2f".format(distance)} km
            Time Taken: ${minutes} min
            Fuel Added: (see fuel logs)
            Maintenance Cost: (see maint logs)
        """.trimIndent()

        binding.confirmSubmitBtn.setOnClickListener {
            Toast.makeText(requireContext(), "Submitted!", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack(R.id.homeFragment, false)
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
