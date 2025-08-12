package com.rxdindia.templevehicletracker.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.rxdindia.templevehicletracker.Network
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class LoginUiState(val success: Boolean, val message: String = "", val userId: Int? = null, val role: String? = null)

class LoginViewModel : ViewModel() {
    private val _loginState = MutableLiveData<LoginUiState>()
    val loginState: LiveData<LoginUiState> = _loginState

    fun login(username: String, password: String) {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    Network.apiService.login(username, password)
                }
                if (response.success) {
                    _loginState.postValue(LoginUiState(true, "", response.userId, response.role))
                } else {
                    _loginState.postValue(LoginUiState(false, "Invalid credentials"))
                }
            } catch (e: Exception) {
                Log.e("LoginViewModel", "login failed", e)
                _loginState.postValue(LoginUiState(false, e.message ?: "Login failed"))
            }
        }
    }
}
