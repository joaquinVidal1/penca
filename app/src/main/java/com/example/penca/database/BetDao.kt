package com.example.penca.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BetDao {
    @Query("select * from betsTable t where t.userId= :id")
    fun getBetsForUser(id: Int): LiveData<List<DBBet>>

    @Query("select * from betsTable t where t.userId=:userId and t.matchId=:matchId")
    fun getBetForMatch(matchId: Int, userId: Int): DBBet

    @Query("DELETE FROM betsTable WHERE userId=:userId")
    fun emptyTableForUser(userId: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(bets: List<DBBet>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBet(bet: DBBet)

    @Query("DELETE FROM betsTable WHERE userId=:userId and matchId=:matchId")
    fun deleteBet(userId: Int, matchId: Int)

    @Query("DELETE FROM betsTable WHERE userId!=:userId")
    fun deleteOtherUsersBets(userId: Int)

}