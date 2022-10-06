package com.example.penca.di

import com.example.penca.network.MatchService
import com.example.penca.network.UserNetwork
import com.example.penca.network.UserService
import com.example.penca.repository.AuthRepository
import com.example.penca.utils.PreferenceHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
class ApiModule {

    @Provides
    fun provideUserApi(): UserService {
        return UserNetwork.getBuilder(
            getToken = { "null" },
            on401Response = { }
        ).create(UserService::class.java)
    }

    @Provides
    fun provideMatchApi(authRepo: AuthRepository, preferenceHelper: PreferenceHelper): MatchService {
        return UserNetwork.getBuilder(
            getToken = { preferenceHelper.getPreferences().getString("token", "null") },
            on401Response = { authRepo.logOut() }
        ).create(MatchService::class.java)
    }
}