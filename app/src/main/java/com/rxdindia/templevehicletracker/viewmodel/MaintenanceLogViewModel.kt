package com.rxdindia.templevehicletracker.viewmodel

import androidx.lifecycle.*
import com.rxdindia.templevehicletracker.Network
import com.rxdindia.templevehicletracker.entity.MaintenanceLog
import kotlinx.coroutines.launch

class MaintenanceLogViewModel : ViewModel() {
    private val _logs = MutableLiveData<List<MaintenanceLog>>()
    val logs: LiveData<List<MaintenanceLog>> = _logs

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadMaintenanceLogs() {
        viewModelScope.launch {
            try {
                val result = Network.apiService.getMaintenanceLogs()
                _logs.postValue(result)
            } catch (e: Exception) {
                _error.postValue(e.message ?: "Failed to load maintenance logs")
            }
        }
    }
}
