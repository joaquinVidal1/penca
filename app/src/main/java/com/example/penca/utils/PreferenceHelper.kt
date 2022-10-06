package com.example.penca.utils

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PreferenceHelper @Inject constructor(
    @ApplicationContext private val appContext: Context
) {
    fun getPreferences(): SharedPreferences {
        return appContext.getSharedPreferences("algo", Context.MODE_PRIVATE)
    }
}