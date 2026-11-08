package com.nhom.movieapp.auth.data

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/** Lưu phiên đăng nhập vào EncryptedSharedPreferences (mã hóa bằng khóa trong Android Keystore). */
class SessionManager(context: Context) {

    private val prefs = EncryptedSharedPreferences.create(
        context,
        "secure_session",
        MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    val email: String? get() = prefs.getString(KEY_EMAIL, null)
    val loginTime: Long get() = prefs.getLong(KEY_TIME, 0L)

    fun save(email: String) {
        prefs.edit()
            .putString(KEY_EMAIL, email)
            .putLong(KEY_TIME, System.currentTimeMillis())
            .apply()
    }

    fun clear() = prefs.edit().clear().apply()

    private companion object {
        const val KEY_EMAIL = "email"
        const val KEY_TIME = "login_time"
    }
}