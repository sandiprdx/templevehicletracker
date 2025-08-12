package com.rxdindia.templevehicletracker.ui

import android.Manifest
import android.location.Location
import android.os.Bundle
import android.view.*
import android.widget.EditText
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.*
import com.rxdindia.templevehicletracker.R
import com.rxdindia.templevehicletracker.SessionManager
import com.rxdindia.templevehicletracker.data.TripRepository
import com.rxdindia.templevehicletracker.databinding.FragmentTripTrackingNewBinding
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import kotlin.math.roundToInt

class TripTrackingNewFragment : Fragment() {
    private var _binding: FragmentTripTrackingNewBinding? = null
    private val binding get() = _binding!!

    private var tripId: Int = -1
    private val path = Polyline()
    private lateinit var fused: FusedLocationProviderClient
    private lateinit var req: LocationRequest
    private var lastLocation: Location? = null
    private var totalMeters: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tripId = requireArguments().getInt("tripId")
        fused = LocationServices.getFusedLocationProviderClient(requireContext())
        req = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 60_000L) // 1 min
            .setMinUpdateIntervalMillis(60_000L)
            .setMinUpdateDistanceMeters(5f)
            .build()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTripTrackingNewBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.mapView.setMultiTouchControls(true)
        binding.mapView.overlays.add(path)

        // one-shot move to last location
        fused.lastLocation.addOnSuccessListener { it?.let(::acceptLocation) }

        // Add Fuel (now includes meter reading)
        binding.addFuelBtn.setOnClickListener {
            val dlgView = layoutInflater.inflate(R.layout.dialog_add_fuel, null)
            val qty = dlgView.findViewById<EditText>(R.id.qty)
            val cost = dlgView.findViewById<EditText>(R.id.cost)
            val meter = dlgView.findViewById<EditText>(R.id.meter)
            AlertDialog.Builder(requireContext())
                .setTitle("Add Fuel")
                .setView(dlgView)
                .setPositiveButton("Save") { _, _ ->
                    lifecycleScope.launch {
                        TripRepository.addFuelOnlineOrQueue(
                            tripId = if (tripId > 0) tripId else null,
                            vehicleId = getVehicleIdForCurrentTrip(), // if you keep this around; else pass via args
                            quantity = qty.text.toString().toDoubleOrNull() ?: 0.0,
                            cost = cost.text.toString().toDoubleOrNull() ?: 0.0,
                            meterReading = meter.text.toString().toDoubleOrNull() ?: 0.0
                        )
                    }
                }.setNegativeButton("Cancel", null).show()
        }

        // Add Maintenance
        binding.addMaintenanceBtn.setOnClickListener {
            val dlgView = layoutInflater.inflate(R.layout.dialog_add_maintenance, null)
            val desc = dlgView.findViewById<EditText>(R.id.desc)
            val cost = dlgView.findViewById<EditText>(R.id.cost)
            AlertDialog.Builder(requireContext())
                .setTitle("Add Maintenance")
                .setView(dlgView)
                .setPositiveButton("Save") { _, _ ->
                    lifecycleScope.launch {
                        TripRepository.addMaintenanceOnlineOrQueue(
                            vehicleId = getVehicleIdForCurrentTrip(),
                            description = desc.text.toString(),
                            cost = cost.text.toString().toDoubleOrNull() ?: 0.0
                        )
                    }
                }.setNegativeButton("Cancel", null).show()
        }

        // End Trip -> go to end screen with computed distance
        binding.endTripBtn.setOnClickListener {
            findNavController().navigate(
                R.id.action_tripTrackingFragment_to_endTripFragment,
                bundleOf(
                    "tripId" to tripId,
                    "approxDistanceKm" to (totalMeters / 1000.0)
                )
            )
        }
    }

    override fun onStart() {
        super.onStart()
        startUpdates()
    }

    override fun onStop() {
        super.onStop()
        stopUpdates()
    }

    @Suppress("MissingPermission")
    private fun startUpdates() {
        fused.requestLocationUpdates(req, callback, requireActivity().mainLooper)
    }

    private fun stopUpdates() {
        fused.removeLocationUpdates(callback)
    }

    private val callback = object : LocationCallback() {
        override fun onLocationResult(r: LocationResult) {
            r.lastLocation?.let(::acceptLocation)
        }
    }

    private fun acceptLocation(loc: Location) {
        // draw path
        val gp = GeoPoint(loc.latitude, loc.longitude)
        if (path.actualPoints.isEmpty()) {
            binding.mapView.controller.setZoom(16.0)
            binding.mapView.controller.setCenter(gp)
        } else {
            lastLocation?.let { prev ->
                totalMeters += prev.distanceTo(loc).toDouble()
            }
        }
        lastLocation = loc
        path.addPoint(gp)
        val marker = Marker(binding.mapView).apply {
            position = gp
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            title = "You"
        }
        binding.mapView.overlays.add(marker)
        binding.mapView.invalidate()

        // push one breadcrumb to server when tripId is real
        if (tripId > 0) {
            viewLifecycleOwner.lifecycleScope.launch {
                TripRepository.addTripPointIfOnline(
                    tripId = tripId,
                    lat = loc.latitude,
                    lon = loc.longitude,
                    speed = if (loc.hasSpeed()) loc.speed.toDouble() else null
                )
            }
        }
    }

    private fun getVehicleIdForCurrentTrip(): Int {
        // simplest: if you saved vehicleId in Session or in a singleton when starting trip, return it here
        // for now, reuse a persisted selection if you have one; else hardcode or add to nav args
        return SessionManager.getUserId(requireContext()) // <-- replace this with YOUR persisted vehicleId
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
