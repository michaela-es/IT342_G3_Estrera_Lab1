package com.systems.mobileauth.data.remote

import com.systems.mobileauth.data.models.AuthResponse
import com.systems.mobileauth.data.models.LoginRequest
import com.systems.mobileauth.data.models.ProfileResponse
import com.systems.mobileauth.data.models.RegisterRequest
import retrofit2.http.*

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @GET("user/me")
    suspend fun getProfile(): ProfileResponse
}