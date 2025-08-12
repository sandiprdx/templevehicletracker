package com.rxdindia.templevehicletracker.viewmodel

import androidx.lifecycle.*
import com.rxdindia.templevehicletracker.Network
import com.rxdindia.templevehicletracker.entity.Trip
import kotlinx.coroutines.launch

class TripViewModel : ViewModel() {
    private val _trips = MutableLiveData<List<Trip>>()
    val trips: LiveData<List<Trip>> = _trips

    private val _startTripResult = MutableLiveData<Result<Trip>>()
    val startTripResult: LiveData<Result<Trip>> = _startTripResult

    fun loadTrips() {
        viewModelScope.launch {
            try {
                val result = Network.apiService.getTrips()
                _trips.postValue(result)
            } catch (e: Exception) {
                // handle/log
            }
        }
    }

    fun startTrip(trip: Trip) {
        viewModelScope.launch {
            try {
                val created = Network.apiService.createTrip(trip)
                _startTripResult.postValue(Result.success(created))
            } catch (e: Exception) {
                _startTripResult.postValue(Result.failure(e))
            }
        }
    }
}
