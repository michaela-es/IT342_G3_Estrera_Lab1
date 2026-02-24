package com.systems.mobileauth.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.systems.mobileauth.data.local.TokenManager
import com.systems.mobileauth.data.repository.AuthRepository
import com.systems.mobileauth.data.models.AuthResponse
import com.systems.mobileauth.data.models.ProfileResponse
import kotlinx.coroutines.launch

enum class AuthScreen {
    LOGIN, REGISTER, PROFILE
}

class AuthViewModel : ViewModel() {
    private val _currentScreen = MutableLiveData(AuthScreen.LOGIN)
    val currentScreen: LiveData<AuthScreen> = _currentScreen

    private val _profile = MutableLiveData<ProfileResponse?>()
    val profile: LiveData<ProfileResponse?> = _profile

    private val _authResponse = MutableLiveData<AuthResponse?>()
    val authResponse: LiveData<AuthResponse?> = _authResponse

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _successMessage = MutableLiveData<String?>()
    val successMessage: LiveData<String?> = _successMessage
    lateinit var authRepository: AuthRepository
    lateinit var tokenManager: TokenManager

    fun showLogin() {
        _currentScreen.value = AuthScreen.LOGIN
    }

    fun showRegister() {
        _currentScreen.value = AuthScreen.REGISTER
    }

    fun showProfile() {
        _currentScreen.value = AuthScreen.PROFILE
        getProfile()
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = authRepository.login(username, password)
                _authResponse.value = response
                showProfile()
            } catch (e: Exception) {
                _error.value = e.message ?: "Login failed"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun register(username: String, email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = authRepository.register(username, email, password)
                _authResponse.value = response

                val message = try {
                    val gson = com.google.gson.Gson()
                    val jsonString = gson.toJson(response)
                    val jsonObject = gson.fromJson(jsonString, com.google.gson.JsonObject::class.java)
                    jsonObject.get("message")?.asString
                } catch (e: Exception) {
                    null
                }

                _successMessage.value = message ?: "Registration successful"
            } catch (e: Exception) {
                _error.value = e.message ?: "Registration failed"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = authRepository.getProfile()
                _profile.value = result
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load profile"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        authRepository.logout()
        _profile.value = null
        _authResponse.value = null
        showLogin()
    }

    fun checkForToken() {
        viewModelScope.launch {
            try {
                val result = authRepository.getProfile()
                _profile.value = result
                showProfile()
            } catch (e: Exception) {
                authRepository.logout()
                showLogin()
            }
        }
    }
}