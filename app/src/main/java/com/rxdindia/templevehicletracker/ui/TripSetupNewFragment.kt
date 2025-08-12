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
import com.rxdindia.templevehicletracker.data.TripRepository
import com.rxdindia.templevehicletracker.databinding.FragmentTripSetupNewBinding
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
    private var vehicleNumber: String? = null
    private var startKm: Double? = null
    private var photoUri: Uri? = null
    private var photoFile: File? = null
    private var destinationId: Int? = null
    private var customDestination: String? = null

    private val requestCameraPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) openCamera() else Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_LONG).show()
    }

    private val takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) { ok ->
        Toast.makeText(requireContext(), if (ok) "Meter photo saved" else "Camera cancelled", Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vehicleId = requireArguments().getInt("vehicleId")
        vehicleNumber = requireArguments().getString("vehicleNumber")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTripSetupNewBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun now(): String = java.time.LocalDateTime.now().toString().replace('T', ' ')

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Prevent starting another trip if active
        TripSession.activeTripId(requireContext())?.let {
            Toast.makeText(requireContext(), "Trip already active. End it first.", Toast.LENGTH_LONG).show()
            findNavController().navigate(R.id.action_tripSetupFragment_to_tripTrackingFragment, bundleOf("tripId" to it))
            return
        }

        // Load destinations
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
            } catch (_: Exception) {}
        }

        binding.startMeterPhotoButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                requestCameraPermission.launch(Manifest.permission.CAMERA)
            }
        }

        binding.startTripButton.setOnClickListener {
            startKm = binding.manualKmInput.text.toString().toDoubleOrNull()
            customDestination = binding.customDestinationInput.text?.toString()?.takeIf { it.isNotBlank() }
            val startTime = now()

            lifecycleScope.launch {
                val serverId = TripRepository.startTripOnlineOrQueue(
                    context = requireContext(),
                    vehicleId = vehicleId,
                    destinationId = destinationId,
                    startMeter = startKm,
                    startTime = startTime
                )

                TripSession.setActive(
                    ctx = requireContext(),
                    tripId = serverId,
                    vehicleId = vehicleId,
                    startKm = startKm,
                    startPhoto = photoFile?.absolutePath,
                    destId = destinationId,
                    destCustom = customDestination,
                    startTime = startTime
                )

                findNavController().navigate(
                    R.id.action_tripSetupFragment_to_tripTrackingFragment,
                    bundleOf("tripId" to serverId)
                )
            }
        }
    }

    private fun openCamera() {
        val created = ImageUriFactory.create(requireContext())
        photoUri = created.uri
        photoFile = created.file
        takePicture.launch(photoUri)
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
