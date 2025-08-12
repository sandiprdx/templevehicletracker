package com.rxdindia.templevehicletracker.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.rxdindia.templevehicletracker.databinding.FragmentMaintenanceLogBinding
import com.rxdindia.templevehicletracker.entity.MaintenanceLog
import com.rxdindia.templevehicletracker.viewmodel.MaintenanceLogViewModel

class MaintenanceLogFragment : Fragment() {
    private var _binding: FragmentMaintenanceLogBinding? = null
    private val binding get() = _binding!!
    private val viewModel = MaintenanceLogViewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMaintenanceLogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.maintenanceRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.maintenanceRecyclerView.adapter = MaintenanceLogAdapter(emptyList())

        viewModel.logs.observe(viewLifecycleOwner, Observer { logs ->
            if (logs != null) binding.maintenanceRecyclerView.adapter = MaintenanceLogAdapter(logs)
        })

        viewModel.loadMaintenanceLogs()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class MaintenanceLogAdapter(private val logs: List<MaintenanceLog>) : androidx.recyclerview.widget.RecyclerView.Adapter<MaintenanceLogAdapter.ViewHolder>() {
    class ViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_2, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val log = logs[position]
        holder.itemView.findViewById<android.widget.TextView>(android.R.id.text1).text = log.maintenanceDate
        holder.itemView.findViewById<android.widget.TextView>(android.R.id.text2).text = "${log.description} — ₹${log.cost}"
    }

    override fun getItemCount() = logs.size
}
