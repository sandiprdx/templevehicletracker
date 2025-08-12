package com.rxdindia.templevehicletracker.viewmodel

import androidx.lifecycle.*
import com.rxdindia.templevehicletracker.Network
import com.rxdindia.templevehicletracker.entity.Vehicle
import kotlinx.coroutines.launch

class VehicleViewModel : ViewModel() {
    private val _vehicles = MutableLiveData<List<Vehicle>>()
    val vehicles: LiveData<List<Vehicle>> = _vehicles

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadVehicles() {
        viewModelScope.launch {
            try {
                val result = Network.apiService.getVehicles()
                _vehicles.postValue(result)
            } catch (e: Exception) {
                _error.postValue(e.message ?: "Failed to load vehicles")
            }
        }
    }
}
