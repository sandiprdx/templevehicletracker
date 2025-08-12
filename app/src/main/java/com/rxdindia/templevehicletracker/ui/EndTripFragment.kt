package com.rxdindia.templevehicletracker.ui

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.*
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
import com.rxdindia.templevehicletracker.databinding.FragmentEndTripBinding
import com.rxdindia.templevehicletracker.entity.Trip
import com.rxdindia.templevehicletracker.util.ImageUriFactory
import com.rxdindia.templevehicletracker.util.TripSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.time.Duration
import java.time.LocalDateTime

class EndTripFragment : Fragment() {
    private var _binding: FragmentEndTripBinding? = null
    private val binding get() = _binding!!

    private var tripId: Int = -1
    private var trackedMeters: Double = 0.0

    private var endPhotoUri: Uri? = null
    private var endPhotoFile: File? = null

    private val requestCameraPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) openEndCamera()
        else Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_LONG).show()
    }

    private val takeEndPicture = registerForActivityResult(ActivityResultContracts.TakePicture()) { ok ->
        if (ok) Toast.makeText(requireContext(), "End meter photo saved: ${endPhotoFile?.name}", Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            tripId = it.getInt("tripId")
            trackedMeters = it.getDouble("trackedMeters", 0.0)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentEndTripBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun now(): String = java.time.LocalDateTime.now().toString().replace('T',' ')

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val startTimeStr = TripSession.startTime(requireContext())
        val durMin = try {
            val st = LocalDateTime.parse((startTimeStr ?: now()).replace(' ', 'T'))
            Duration.between(st, LocalDateTime.parse(now().replace(' ', 'T'))).toMinutes()
        } catch (_: Exception) { 0L }

        binding.autoInfo.text = "Auto date: ${now()}\nDuration: ${durMin} min"

        binding.endMeterPhotoButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
                openEndCamera()
            } else {
                requestCameraPermission.launch(Manifest.permission.CAMERA)
            }
        }

        binding.submitEndBtn.setOnClickListener {
            val endKm = binding.manualEndKmInput.text.toString().toDoubleOrNull()
            val startKm = TripSession.startKm(requireContext())
            val distanceFromKms = if (startKm != null && endKm != null) (endKm - startKm).coerceAtLeast(0.0) else null
            val distanceFromMeters = trackedMeters
            val finalDistance = distanceFromKms ?: (distanceFromMeters / 1000.0)

            lifecycleScope.launch {
                try {
                    if (tripId > 0) {
                        withContext(Dispatchers.IO) {
                            Network.apiService.updateTrip(
                                tripId,
                                Trip(
                                    tripId = tripId,
                                    userId = SessionManager.getUserId(requireContext()),
                                    vehicleId = 0,
                                    startTime = startTimeStr ?: "",
                                    endTime = now(),
                                    totalDistance = finalDistance,
                                    status = "COMPLETED"
                                )
                            )
                        }
                    }
                    TripSession.clear(requireContext())
                    findNavController().navigate(
                        R.id.action_endTripFragment_to_tripSummaryFragment,
                        bundleOf(
                            "tripId" to tripId,
                            "distanceKm" to (finalDistance),
                            "durationMin" to durMin
                        )
                    )
                } catch (_: Exception) {
                    Toast.makeText(requireContext(), "Offline: ended locally, will sync later", Toast.LENGTH_LONG).show()
                    TripSession.clear(requireContext())
                    findNavController().navigate(
                        R.id.action_endTripFragment_to_tripSummaryFragment,
                        bundleOf("tripId" to tripId, "distanceKm" to finalDistance, "durationMin" to durMin)
                    )
                }
            }
        }
    }

    private fun openEndCamera() {
        val created = ImageUriFactory.createImageUri(requireContext())
        endPhotoUri = created.uri
        endPhotoFile = created.file
        takeEndPicture.launch(endPhotoUri)
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
