package com.systems.mobileauth.data.models

data class LoginRequest(
    var usernameOrEmail: String? = null,
    var password: String? = null
)