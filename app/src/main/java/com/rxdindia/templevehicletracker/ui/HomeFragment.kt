package com.rxdindia.templevehicletracker.ui

import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.rxdindia.templevehicletracker.Network
import com.rxdindia.templevehicletracker.TVTApp
import com.rxdindia.templevehicletracker.databinding.FragmentHomeBinding
import com.rxdindia.templevehicletracker.recent.RecentTrip
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var selectedVehicleId: Int? = null
    private var selectedVehicleNumber: String? = null
    private var vehicleNumbers: List<String> = emptyList()

    private val adapter = RecentTripAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // vehicles dropdown
        lifecycleScope.launch {
            try {
                val vehicles = withContext(Dispatchers.IO) { Network.apiService.getVehicles() }
                vehicleNumbers = vehicles.map { it.vehicleNumber }
                val spAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, vehicleNumbers)
                binding.vehicleDropdown.adapter = spAdapter
                binding.vehicleDropdown.onItemSelectedListener = object: android.widget.AdapterView.OnItemSelectedListener{
                    override fun onItemSelected(parent: android.widget.AdapterView<*>, v: View?, pos: Int, id: Long) {
                        selectedVehicleId = vehicles[pos].vehicleId
                        selectedVehicleNumber = vehicles[pos].vehicleNumber
                    }
                    override fun onNothingSelected(p0: android.widget.AdapterView<*>?) {}
                }
            } catch (_: Exception) { }
        }

        // recent trips list
        binding.recentsRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.recentsRecycler.adapter = adapter
        loadRecents()

        binding.startTripFlowBtn.setOnClickListener {
            val vid = selectedVehicleId ?: return@setOnClickListener
            val vnum = selectedVehicleNumber ?: "Vehicle"
            androidx.navigation.fragment.findNavController(this@HomeFragment).navigate(
                com.rxdindia.templevehicletracker.R.id.action_homeFragment_to_tripSetupFragment,
                bundleOf("vehicleId" to vid, "vehicleNumber" to vnum)
            )
        }
    }

    private fun loadRecents() {
        lifecycleScope.launch {
            val recents = withContext(Dispatchers.IO) { TVTApp.db.recentTripDao().latest() }
            adapter.submit(recents)
            binding.emptyText.visibility = if (recents.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        loadRecents()
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
