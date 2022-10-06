package com.example.penca.di

import android.content.Context
import android.content.SharedPreferences
import com.example.penca.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
class PreferencesModule {

    @Provides
    fun provideSharedPreferences(@ApplicationContext appContext: Context): SharedPreferences {
        return appContext.getSharedPreferences(
            appContext.getString(R.string.shared_preferences_name),
            Context.MODE_PRIVATE
        )
    }
}