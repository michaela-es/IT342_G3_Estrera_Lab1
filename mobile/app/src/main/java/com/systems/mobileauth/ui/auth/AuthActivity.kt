package com.systems.mobileauth.ui.auth

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.systems.mobileauth.R
import com.systems.mobileauth.ui.viewmodel.AuthScreen
import com.systems.mobileauth.ui.viewmodel.AuthViewModel
import androidx.activity.viewModels
import com.systems.mobileauth.data.local.TokenManager
import com.systems.mobileauth.data.remote.RetrofitClient
import com.systems.mobileauth.data.repository.AuthRepository

class AuthActivity : AppCompatActivity() {

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        val tokenManager = TokenManager(this)
        val apiService = RetrofitClient.createApiService(tokenManager)
        val authRepository = AuthRepository(apiService, tokenManager)

        viewModel.authRepository = authRepository
        viewModel.tokenManager = tokenManager
        viewModel.checkForToken()
        viewModel.error.observe(this) { error ->
            error?.let {
                android.widget.Toast.makeText(this, it, android.widget.Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.successMessage.observe(this) { message ->
            message?.let {
                android.widget.Toast.makeText(this, it, android.widget.Toast.LENGTH_SHORT).show()
            }
        }

        val switchLink: TextView = findViewById(R.id.tv_switch)

        viewModel.currentScreen.observe(this) { screen ->
            val fragment = when (screen) {
                AuthScreen.LOGIN -> LoginFragment()
                AuthScreen.REGISTER -> RegisterFragment()
                AuthScreen.PROFILE -> ProfileFragment()
            }

            supportFragmentManager.commit {
                replace(R.id.auth_container, fragment)
            }

            when (screen) {
                AuthScreen.LOGIN -> {
                    switchLink.text = "Don't have an account? Register"
                    switchLink.visibility = View.VISIBLE
                }
                AuthScreen.REGISTER -> {
                    switchLink.text = "Already have an account? Login"
                    switchLink.visibility = View.VISIBLE
                }
                AuthScreen.PROFILE -> {
                    switchLink.visibility = View.GONE
                }
            }
        }

        switchLink.setOnClickListener {
            when (viewModel.currentScreen.value) {
                AuthScreen.LOGIN -> viewModel.showRegister()
                AuthScreen.REGISTER -> viewModel.showLogin()
                else -> {}
            }
        }
    }
}