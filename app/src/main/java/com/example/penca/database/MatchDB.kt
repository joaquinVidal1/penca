package com.example.penca.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.penca.domain.entities.MatchEvent
import com.example.penca.domain.entities.Team
import java.time.LocalDate

@Entity(tableName = "matchesTable")
@TypeConverters(Converters::class)
class MatchDB(
    @PrimaryKey
    val matchId: Int,
    val homeTeamId: Int,
    val hoomeTeamName: String,
    val homeTeamLogo: Int,
    val awayTeamId: Int,
    val awayTeamName: String,
    val awayTeamLogo: Int,
    val date: LocalDate,
    val goalsLocal: Int? = null,
    val goalsAway: Int? = null,
)