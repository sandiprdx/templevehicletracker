package com.rxdindia.templevehicletracker

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.rxdindia.templevehicletracker.databinding.ActivityLoginBinding
import com.rxdindia.templevehicletracker.viewmodel.LoginViewModel


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // If a session already exists, go straight to MainActivity
        if (SessionManager.isLoggedIn(this)) {
            startMainAndFinish()
            return
        }

        binding.loginButton.setOnClickListener {
            val username = binding.usernameEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString()
            if (username.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            binding.loginButton.isEnabled = false
            loginViewModel.login(username, password)
        }

        loginViewModel.loginState.observe(this) { state ->
            binding.loginButton.isEnabled = true
            if (state.success) {
                // Persist session
                SessionManager.saveLogin(this, state.userId ?: -1, state.role)
                startMainAndFinish()
                com.rxdindia.templevehicletracker.sync.SyncScheduler.schedule()
            } else {
                Toast.makeText(this, state.message.ifBlank { "Login failed" }, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startMainAndFinish() {
        startActivity(Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        })
        finish()
    }
}
