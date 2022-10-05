package com.example.penca.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface MatchDao {
    @Query("select * from matchesTable")
    fun getMatches(): LiveData<List<DBMatch>>

    @Query("DELETE FROM matchesTable")
    fun emptyTable()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(matches: List<DBMatch>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMatch(match: DBMatch)

    @Transaction
    fun emptyAndInsert(matches: List<DBMatch>){
        emptyTable()
        insertAll(matches)
    }
}