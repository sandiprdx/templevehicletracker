package com.rxdindia.templevehicletracker.viewmodel

import androidx.lifecycle.*
import com.rxdindia.templevehicletracker.Network
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class LoginUiState(val success: Boolean, val message: String = "", val userId: Int? = null, val role: String? = null)
data class LoginResponse(val success: Boolean, val userId: Int, val role: String)

class LoginViewModel : ViewModel() {
    private val _loginState = MutableLiveData<LoginUiState>()
    val loginState: LiveData<LoginUiState> = _loginState

    fun login(username: String, password: String) {
        viewModelScope.launch {
            try {
                val resp = withContext(Dispatchers.IO) { Network.apiService.login(username, password) }
                if (resp.success) {
                    _loginState.postValue(LoginUiState(true, "", resp.userId, resp.role))
                } else {
                    _loginState.postValue(LoginUiState(false, "Invalid credentials"))
                }
            } catch (e: Exception) {
                _loginState.postValue(LoginUiState(false, e.message ?: "Login failed"))
            }
        }
    }
}
