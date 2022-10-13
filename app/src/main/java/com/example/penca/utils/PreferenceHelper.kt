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

    // TODO podes crear una variable TOKEN_KEY con el valor "token", en vez de poner "token" cada vez
    fun insertToken(token: String) {
        val editor = this.getPreferences().edit()
        editor.putString("token", token)
        editor.apply()
    }

    fun getToken(): String {
        // TODO porque tiene null comillas?, si queres usar null, es sin comillas, si queres mostrar que no hay token, usa un String vacio
        return this.getPreferences().getString("token", "null") ?: "null"
    }

    fun removeToken() {
        val editor = this.getPreferences().edit()
        editor.remove("token")
        editor.apply()
    }

}