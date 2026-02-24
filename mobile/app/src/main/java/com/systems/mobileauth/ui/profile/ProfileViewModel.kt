package com.systems.mobileauth.ui.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.systems.mobileauth.data.models.ProfileResponse
import com.systems.mobileauth.data.repository.AuthRepository
import kotlinx.coroutines.launch

class ProfileViewModel(private val repo: AuthRepository) : ViewModel() {
    val user = MutableLiveData<ProfileResponse>()

    fun loadProfile() {
        viewModelScope.launch {
            try {
                val result = repo.getProfile()
                user.value = result
            } catch (e: Exception) {
            }
        }
    }
}