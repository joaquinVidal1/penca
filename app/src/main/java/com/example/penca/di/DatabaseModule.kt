package com.example.penca.di

import android.content.Context
import com.example.penca.database.MatchDao
import com.example.penca.database.PencaDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): PencaDatabase {
        return PencaDatabase.getPencaDatabase(appContext)
    }

    @Provides
    fun provideMatchDao(database: PencaDatabase): MatchDao {
        return database.matchDao
    }

}