package com.example.penca.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.penca.domain.entities.MatchEvent
import com.example.penca.domain.entities.Team
import java.time.LocalDate

@Entity(tableName = "matchesTable")
@TypeConverters(Converters::class)
class DBMatch(
    @PrimaryKey
    val matchId: Int,
    val homeTeamId: Int,
    val homeTeamName: String,
    val homeTeamLogo: String,
    val awayTeamId: Int,
    val awayTeamName: String,
    val awayTeamLogo: String,
    val date: LocalDate,
    val goalsHome: Int? = null,
    val goalsAway: Int? = null,
    val predictedGoalsHome: Int? = null,
    val predictedGoalsAway: Int? = null
)
