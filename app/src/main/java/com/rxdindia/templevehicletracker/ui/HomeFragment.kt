package com.rxdindia.templevehicletracker.ui

import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.rxdindia.templevehicletracker.Network
import com.rxdindia.templevehicletracker.R
import com.rxdindia.templevehicletracker.TVTApp
import com.rxdindia.templevehicletracker.databinding.FragmentHomeBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var selectedVehicleId: Int? = null
    private var selectedVehicleNumber: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(v: View, s: Bundle?) {
        // vehicles
        lifecycleScope.launch {
            try {
                val vehicles = withContext(Dispatchers.IO) { Network.apiService.getVehicles() }
                val labels = vehicles.map { "${it.vehicleNumber} (${it.vehicleType ?: ""})" }
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, labels)
                binding.vehicleDropdown.adapter = adapter
                binding.vehicleDropdown.onItemSelectedListener = object: android.widget.AdapterView.OnItemSelectedListener{
                    override fun onItemSelected(parent: android.widget.AdapterView<*>, v: View?, pos: Int, id: Long) {
                        selectedVehicleId = vehicles[pos].vehicleId
                        selectedVehicleNumber = vehicles[pos].vehicleNumber
                    }
                    override fun onNothingSelected(p0: android.widget.AdapterView<*>?) {}
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Failed to load vehicles", Toast.LENGTH_SHORT).show()
            }
        }

        // recent trips
        lifecycleScope.launch {
            val recents = withContext(Dispatchers.IO) { TVTApp.db.recentTripDao().recent() }
            binding.recentList.adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1,
                recents.map { "${it.vehicleNumber}: ${it.distanceKm} km • ${it.summary}" }
            )
        }

        binding.startTripBtn.setOnClickListener {
            val vid = selectedVehicleId
            val vnum = selectedVehicleNumber
            if (vid == null) {
                Toast.makeText(requireContext(), "Select vehicle", Toast.LENGTH_SHORT).show(); return@setOnClickListener
            }
            findNavController().navigate(
                R.id.action_homeFragment_to_tripSetupFragment,
                bundleOf("vehicleId" to vid, "vehicleNumber" to vnum)
            )
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
