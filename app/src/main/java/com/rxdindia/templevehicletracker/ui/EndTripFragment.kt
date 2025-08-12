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
import androidx.navigation.fragment.findNavController
import com.rxdindia.templevehicletracker.R
import com.rxdindia.templevehicletracker.databinding.FragmentEndTripBinding
import com.rxdindia.templevehicletracker.util.ImageUriFactory
import java.io.File

class EndTripFragment : Fragment() {
    private var _binding: FragmentEndTripBinding? = null
    private val binding get() = _binding!!
    private var tripId: Int = -1
    private var photoUri: Uri? = null
    private var photoFile: File? = null

    private val requestCamera = registerForActivityResult(ActivityResultContracts.RequestPermission()) { ok ->
        if (ok) openCamera() else Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_LONG).show()
    }
    private val takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) { ok ->
        Toast.makeText(requireContext(), if (ok) "End meter photo saved" else "Cancelled", Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tripId = requireArguments().getInt("tripId")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentEndTripBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun now(): String = java.time.LocalDateTime.now().toString().replace('T',' ')

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.autoInfo.text = "Auto date: ${now()}"

        binding.endMeterPhotoButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else requestCamera.launch(Manifest.permission.CAMERA)
        }

        binding.submitEndBtn.setOnClickListener {
            val endKm = binding.manualEndKmInput.text.toString().toDoubleOrNull()
            findNavController().navigate(
                R.id.action_endTripFragment_to_tripSummaryFragment,
                bundleOf("tripId" to tripId, "endKm" to endKm)
            )
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
