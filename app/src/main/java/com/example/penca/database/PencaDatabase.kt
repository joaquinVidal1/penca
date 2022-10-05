package com.example.penca.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [DBMatch::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class PencaDatabase: RoomDatabase() {

    abstract val matchDao: MatchDao

    companion object {
        @Volatile
        private lateinit var PENCADBINSTANCE: PencaDatabase

        fun getPencaDatabase(context: Context): PencaDatabase {
            synchronized(this) {
                if (!::PENCADBINSTANCE.isInitialized) {
                    PENCADBINSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        PencaDatabase::class.java,
                        "items"
                    ).build()
                }
            }
            return PENCADBINSTANCE
        }
    }
}