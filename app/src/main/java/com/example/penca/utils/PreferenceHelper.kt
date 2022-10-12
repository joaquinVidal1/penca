package com.example.penca.utils

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PreferenceHelper @Inject constructor(
    @ApplicationContext private val appContext: Context
) {
    private fun getPreferences(): SharedPreferences {
        return appContext.getSharedPreferences("PENCA_PREFERENCES", Context.MODE_PRIVATE)
    }

    fun insertToken(token: String) {
        val editor = this.getPreferences().edit()
        editor.putString("token", token)
        editor.apply()
    }

    fun getToken(): String {
        return this.getPreferences().getString("token", "null") ?: "null"
    }

    fun removeToken() {
        val editor = this.getPreferences().edit()
        editor.remove("token")
        editor.apply()
    }

}