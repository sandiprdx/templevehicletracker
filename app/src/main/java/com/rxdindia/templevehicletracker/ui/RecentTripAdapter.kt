package com.rxdindia.templevehicletracker.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rxdindia.templevehicletracker.databinding.ItemRecentTripBinding
import com.rxdindia.templevehicletracker.recent.RecentTrip

class RecentTripAdapter : RecyclerView.Adapter<RecentTripAdapter.VH>() {

    private val items = mutableListOf<RecentTrip>()

    fun submit(newItems: List<RecentTrip>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    class VH(val binding: ItemRecentTripBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemRecentTripBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val it = items[position]
        holder.binding.title.text = "${it.vehicleNumber} • ${"%.1f".format(it.distanceKm)} km"
        holder.binding.subtitle.text = "${it.startTime} → ${it.endTime}\n${it.summary}"
    }

    override fun getItemCount(): Int = items.size
}
