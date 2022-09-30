package com.example.penca.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface MatchDao {
    @Query("select * from matchesTable")
    fun getMatches(): LiveData<List<MatchDB>>

    @Query("DELETE FROM matchesTable")
    fun emptyTable()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(matches: List<MatchDB>)

    @Transaction
    fun emptyAndInsert(matches: List<MatchDB>){
        emptyTable()
        insertAll(matches)
    }
}