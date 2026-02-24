package com.systems.mobileauth.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

enum class AuthScreen { LOGIN, REGISTER }

class AuthViewModel : ViewModel() {

    private val _currentScreen = MutableLiveData(AuthScreen.LOGIN)
    val currentScreen: LiveData<AuthScreen> = _currentScreen

    fun showLogin() {
        _currentScreen.value = AuthScreen.LOGIN
    }

    fun showRegister() {
        _currentScreen.value = AuthScreen.REGISTER
    }
}