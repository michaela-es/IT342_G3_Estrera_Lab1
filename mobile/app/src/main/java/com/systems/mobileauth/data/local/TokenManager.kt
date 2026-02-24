package com.systems.mobileauth.data.local

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class TokenManager(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs = EncryptedSharedPreferences.create(
        context,
        "auth_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveAccessToken(token: String?) {
        prefs.edit().putString("ACCESS_TOKEN", token).apply()
    }

    fun getAccessToken(): String? = prefs.getString("ACCESS_TOKEN", null)

    fun saveRefreshToken(token: String?) {
        prefs.edit().putString("REFRESH_TOKEN", token).apply()
    }

    fun getRefreshToken(): String? = prefs.getString("REFRESH_TOKEN", null)

    fun clear() {
        prefs.edit().clear().apply()
    }
}