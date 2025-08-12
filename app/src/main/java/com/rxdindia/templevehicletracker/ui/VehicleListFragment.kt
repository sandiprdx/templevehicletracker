package com.rxdindia.templevehicletracker.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.rxdindia.templevehicletracker.databinding.FragmentVehicleListBinding
import com.rxdindia.templevehicletracker.entity.Vehicle
import com.rxdindia.templevehicletracker.viewmodel.VehicleViewModel

class VehicleListFragment : Fragment() {
    private var _binding: FragmentVehicleListBinding? = null
    private val binding get() = _binding!!
    private val viewModel = VehicleViewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentVehicleListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.vehicleRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.vehicleRecyclerView.adapter = VehicleAdapter(emptyList())

        viewModel.vehicles.observe(viewLifecycleOwner, Observer { vehicles ->
            if (vehicles != null) binding.vehicleRecyclerView.adapter = VehicleAdapter(vehicles)
        })

        viewModel.loadVehicles()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class VehicleAdapter(private val vehicles: List<Vehicle>) : androidx.recyclerview.widget.RecyclerView.Adapter<VehicleAdapter.ViewHolder>() {
    class ViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_2, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val v = vehicles[position]
        holder.itemView.findViewById<android.widget.TextView>(android.R.id.text1).text = v.vehicleNumber
        holder.itemView.findViewById<android.widget.TextView>(android.R.id.text2).text = v.vehicleType ?: ""
    }

    override fun getItemCount() = vehicles.size
}
