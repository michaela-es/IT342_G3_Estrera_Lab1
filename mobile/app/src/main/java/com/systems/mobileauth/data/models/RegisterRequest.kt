package com.systems.mobileauth.data.models

data class RegisterRequest(
    var username: String? = null,
    var email: String? = null,
    var password: String? = null
)