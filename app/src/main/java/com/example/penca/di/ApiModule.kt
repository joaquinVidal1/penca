package com.example.penca.di

import com.example.penca.network.MatchService
import com.example.penca.network.RetrofitFactory
import com.example.penca.network.UserService
import com.example.penca.repository.AuthRepository
import com.example.penca.utils.PreferenceHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class ApiModule {

    @Provides
    @Singleton
    fun provideUserApi(
        preferenceHelper: PreferenceHelper
    ): UserService {
        return RetrofitFactory.getBuilder(
            getToken = {
                preferenceHelper.getToken()
            },
            on401Response = {}
        ).create(UserService::class.java)
    }

    @Provides
    @Singleton
    fun provideMatchApi(
        authRepo: AuthRepository,
        preferenceHelper: PreferenceHelper
    ): MatchService {
        return RetrofitFactory.getBuilder(
            getToken = {
                preferenceHelper.getToken()
            },
            on401Response = {
                authRepo.logOut()
            }
        ).create(MatchService::class.java)
    }
}