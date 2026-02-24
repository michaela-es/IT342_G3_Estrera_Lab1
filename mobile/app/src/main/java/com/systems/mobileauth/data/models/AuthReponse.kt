package com.systems.mobileauth.data.models

data class AuthResponse(
    var accessToken: String? = null,
    var refreshToken: String? = null,
    var tokenType: String = "Bearer",
    var expiresIn: Long? = null,
    var user: UserResponse? = null
)