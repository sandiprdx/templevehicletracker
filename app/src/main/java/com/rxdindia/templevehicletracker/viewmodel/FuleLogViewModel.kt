package com.rxdindia.templevehicletracker.viewmodel

import androidx.lifecycle.*
import com.rxdindia.templevehicletracker.Network
import com.rxdindia.templevehicletracker.entity.FuelLog
import kotlinx.coroutines.launch

class FuelLogViewModel : ViewModel() {
    private val _logs = MutableLiveData<List<FuelLog>>()
    val logs: LiveData<List<FuelLog>> = _logs

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadFuelLogs() {
        viewModelScope.launch {
            try {
                val result = Network.apiService.getFuelLogs()
                _logs.postValue(result)
            } catch (e: Exception) {
                _error.postValue(e.message ?: "Failed to load fuel logs")
            }
        }
    }
}
