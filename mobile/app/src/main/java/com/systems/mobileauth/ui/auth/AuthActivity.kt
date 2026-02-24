package com.systems.mobileauth.ui.auth

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.systems.mobileauth.R
import com.systems.mobileauth.ui.viewmodel.AuthScreen
import com.systems.mobileauth.ui.viewmodel.AuthViewModel
import androidx.activity.viewModels

class AuthActivity : AppCompatActivity() {

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        val switchLink: TextView = findViewById(R.id.tv_switch)

        viewModel.currentScreen.observe(this) { screen ->
            val fragment = when (screen) {
                AuthScreen.LOGIN -> LoginFragment()
                AuthScreen.REGISTER -> RegisterFragment()
            }

            supportFragmentManager.commit {
                replace(R.id.auth_container, fragment)
            }

            switchLink.text = if (screen == AuthScreen.LOGIN)
                "Don't have an account? Register"
            else
                "Already have an account? Login"
        }

        switchLink.setOnClickListener {
            if (viewModel.currentScreen.value == AuthScreen.LOGIN)
                viewModel.showRegister()
            else
                viewModel.showLogin()
        }
    }
}