package com.rxdindia.templevehicletracker.ui

import android.Manifest
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.LocationServices
import com.rxdindia.templevehicletracker.R
import com.rxdindia.templevehicletracker.data.TripRepository
import com.rxdindia.templevehicletracker.databinding.FragmentTripTrackingNewBinding
import com.rxdindia.templevehicletracker.util.LocationUtils
import com.rxdindia.templevehicletracker.util.TripSession
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

class TripTrackingNewFragment : Fragment() {
    private var _binding: FragmentTripTrackingNewBinding? = null
    private val binding get() = _binding!!
    private var tripId: Int = -1
    private val path = Polyline()

    private val handler = Handler(Looper.getMainLooper())
    private val tick = object : Runnable {
        override fun run() {
            capturePoint()
            handler.postDelayed(this, 60_000) // every minute
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tripId = requireArguments().getInt("tripId")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTripTrackingNewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.mapView.setMultiTouchControls(true)
        binding.mapView.overlays.add(path)

        if (!LocationUtils.ensureLocationPermissions(this)) return
        LocationServices.getFusedLocationProviderClient(requireContext())
            .lastLocation.addOnSuccessListener { it?.let { moveTo(it) } }

        binding.addFuelBtn.setOnClickListener { showFuelDialog() }
        binding.addMaintenanceBtn.setOnClickListener { showMaintenanceDialog() }
        binding.endTripBtn.setOnClickListener {
            findNavController().navigate(
                R.id.action_tripTrackingFragment_to_endTripFragment,
                bundleOf("tripId" to tripId)
            )
        }
    }

    override fun onResume() {
        super.onResume()
        handler.post(tick)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(tick)
    }

    private fun capturePoint() {
        if (!hasLocationPerms()) return
        LocationServices.getFusedLocationProviderClient(requireContext())
            .lastLocation
            .addOnSuccessListener { loc ->
                if (loc != null) {
                    lifecycleScope.launch {
                        TripRepository.pushTripPointOnlineOrQueue(
                            context = requireContext(),
                            tripId = tripId,
                            lat = loc.latitude,
                            lon = loc.longitude,
                            speed = loc.speed.toDouble()
                        )
                    }
                    moveTo(loc)
                }
            }
    }

    private fun hasLocationPerms(): Boolean =
        requireContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED

    private fun moveTo(loc: Location) {
        val gp = GeoPoint(loc.latitude, loc.longitude)
        if (path.actualPoints.isEmpty()) {
            binding.mapView.controller.setZoom(16.0)
            binding.mapView.controller.setCenter(gp)
        }
        path.addPoint(gp)
        val marker = Marker(binding.mapView).apply {
            position = gp
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            title = "You"
        }
        binding.mapView.overlays.add(marker)
        binding.mapView.invalidate()
    }

    private fun showFuelDialog() {
        val v = layoutInflater.inflate(R.layout.dialog_add_fuel, null)
        val qty = v.findViewById<EditText>(R.id.qty)
        val cost = v.findViewById<EditText>(R.id.cost)
        AlertDialog.Builder(requireContext())
            .setTitle("Add Fuel")
            .setView(v)
            .setPositiveButton("Save") { _, _ ->
                lifecycleScope.launch {
                    TripRepository.addFuelOnlineOrQueue(
                        context = requireContext(),
                        quantity = qty.text.toString().toDoubleOrNull() ?: 0.0,
                        cost = cost.text.toString().toDoubleOrNull() ?: 0.0,
                        vehicleId = TripSession.vehicleId(requireContext()) ?: return@launch,
                        tripId = tripId
                    )
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showMaintenanceDialog() {
        val v = layoutInflater.inflate(R.layout.dialog_add_maintenance, null)
        val desc = v.findViewById<EditText>(R.id.desc)
        val cost = v.findViewById<EditText>(R.id.cost)
        AlertDialog.Builder(requireContext())
            .setTitle("Add Maintenance")
            .setView(v)
            .setPositiveButton("Save") { _, _ ->
                lifecycleScope.launch {
                    TripRepository.addMaintenanceOnlineOrQueue(
                        context = requireContext(),
                        description = desc.text.toString(),
                        cost = cost.text.toString().toDoubleOrNull() ?: 0.0,
                        vehicleId = TripSession.vehicleId(requireContext()) ?: return@launch
                    )
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
