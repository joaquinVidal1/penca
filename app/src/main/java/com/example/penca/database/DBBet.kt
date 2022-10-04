package com.example.penca.database

import androidx.room.Entity

@Entity(tableName = "betsTable", primaryKeys = ["matchId", "userId"])
class DBBet(
    val matchId: Int,
    val userId: Int,
    val homeGoalsBet: Int,
    val awayGoalsBet: Int
)