package com.rxdindia.templevehicletracker.ui

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.rxdindia.templevehicletracker.Network
import com.rxdindia.templevehicletracker.R
import com.rxdindia.templevehicletracker.SessionManager
import com.rxdindia.templevehicletracker.databinding.FragmentTripSetupNewBinding
import com.rxdindia.templevehicletracker.entity.Trip
import com.rxdindia.templevehicletracker.util.ImageUriFactory
import com.rxdindia.templevehicletracker.util.TripSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class TripSetupNewFragment : Fragment() {
    private var _binding: FragmentTripSetupNewBinding? = null
    private val binding get() = _binding!!

    private var vehicleId: Int = -1
    private var startKm: Double? = null
    private var photoUri: Uri? = null
    private var photoFile: File? = null
    private var destinationId: Int? = null
    private var customDestination: String? = null



    // CAMERA permission
    private val requestCameraPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) openCamera()
        else Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_LONG).show()
    }

    // Take picture
    private val takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) { ok ->
        if (ok) {
            Toast.makeText(requireContext(), "Meter photo saved: ${photoFile?.name}", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Camera cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    private var vehicleNumber: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vehicleId = requireArguments().getInt("vehicleId")
        vehicleNumber = requireArguments().getString("vehicleNumber")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTripSetupNewBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun now(): String = java.time.LocalDateTime.now().toString().replace('T',' ')

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // If a trip is already active, send user to tracking
        TripSession.activeTripId(requireContext())?.let {
            Toast.makeText(requireContext(), "Trip already active. End it first.", Toast.LENGTH_LONG).show()
            findNavController().navigate(
                R.id.action_tripSetupFragment_to_tripTrackingFragment,
                bundleOf("tripId" to it)
            )
            return
        }

        // Populate destinations
        lifecycleScope.launch {
            try {
                val destinations = withContext(Dispatchers.IO) { Network.apiService.getDestinations() }
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, destinations.map { it.name })
                binding.destinationDropdown.adapter = adapter
                binding.destinationDropdown.onItemSelectedListener = object: android.widget.AdapterView.OnItemSelectedListener{
                    override fun onItemSelected(parent: android.widget.AdapterView<*>, v: View?, pos: Int, id: Long) {
                        destinationId = destinations[pos].destinationId
                    }
                    override fun onNothingSelected(p0: android.widget.AdapterView<*>?) {}
                }
            } catch (_: Exception) { }
        }

        // Start meter photo
        binding.startMeterPhotoButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                requestCameraPermission.launch(Manifest.permission.CAMERA)
            }
        }

        // Start trip
        binding.startTripButton.setOnClickListener {
            startKm = binding.manualKmInput.text.toString().toDoubleOrNull()
            customDestination = binding.customDestinationInput.text?.toString()?.takeIf { it.isNotBlank() }
            val startTime = now()
            val trip = Trip(
                userId = SessionManager.getUserId(requireContext()),
                vehicleId = vehicleId,
                startTime = startTime,
                destinationId = destinationId
            )

            lifecycleScope.launch {
                try {
                    val created = withContext(Dispatchers.IO) { Network.apiService.createTrip(trip) }
                    val serverId = created.tripId ?: -1
                    TripSession.setActive(requireContext(), serverId, startKm, photoFile?.absolutePath,
                        destinationId as Double? as Double?, customDestination, startTime)
                    findNavController().navigate(
                        R.id.action_tripSetupFragment_to_tripTrackingFragment,
                        bundleOf("tripId" to serverId)
                    )
                } catch (_: Exception) {
                    TripSession.setActive(requireContext(), -1, startKm, photoFile?.absolutePath, destinationId, customDestination, startTime)
                    Toast.makeText(requireContext(), "Offline: tracking locally, will sync later", Toast.LENGTH_LONG).show()
                    findNavController().navigate(
                        R.id.action_tripSetupFragment_to_tripTrackingFragment,
                        bundleOf("tripId" to -1)
                    )
                }
            }
        }
    }

    private fun openCamera() {
        val created = ImageUriFactory.createImageUri(requireContext())
        photoUri = created.uri
        photoFile = created.file
        takePicture.launch(photoUri)
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
