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
    private val vm: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (SessionManager.isLoggedIn(this)) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        binding.loginButton.setOnClickListener {
            val u = binding.usernameEditText.text.toString().trim()
            val p = binding.passwordEditText.text.toString()
            if (u.isEmpty() || p.isEmpty()) {
                Toast.makeText(this, "Enter username & password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            vm.login(u, p)
        }

        vm.loginState.observe(this) { st ->
            if (st.success) {
                SessionManager.saveLogin(this, st.userId ?: -1, st.role)
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, st.message.ifBlank { "Login failed" }, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
