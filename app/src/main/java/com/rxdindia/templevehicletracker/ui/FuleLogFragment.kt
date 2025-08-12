package com.rxdindia.templevehicletracker.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.rxdindia.templevehicletracker.databinding.FragmentFuelLogBinding
import com.rxdindia.templevehicletracker.entity.FuelLog
import com.rxdindia.templevehicletracker.viewmodel.FuelLogViewModel

class FuelLogFragment : Fragment() {
    private var _binding: FragmentFuelLogBinding? = null
    private val binding get() = _binding!!
    private val viewModel = FuelLogViewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFuelLogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.fuelRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.fuelRecyclerView.adapter = FuelLogAdapter(emptyList())

        viewModel.logs.observe(viewLifecycleOwner, Observer { logs ->
            if (logs != null) binding.fuelRecyclerView.adapter = FuelLogAdapter(logs)
        })
        viewModel.error.observe(viewLifecycleOwner, Observer { err ->
            if (err != null) {
                // show toast or placeholder
            }
        })
        viewModel.loadFuelLogs()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class FuelLogAdapter(private val logs: List<FuelLog>) : androidx.recyclerview.widget.RecyclerView.Adapter<FuelLogAdapter.ViewHolder>() {
    class ViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_2, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val log = logs[position]
        holder.itemView.findViewById<android.widget.TextView>(android.R.id.text1).text = log.fuelDate
        holder.itemView.findViewById<android.widget.TextView>(android.R.id.text2).text = "Qty: ${log.quantity} — ₹${log.cost}"
    }

    override fun getItemCount() = logs.size
}
