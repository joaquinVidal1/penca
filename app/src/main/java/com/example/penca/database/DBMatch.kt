package com.example.penca.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.penca.domain.entities.Bet
import com.example.penca.domain.entities.BetStatus
import com.example.penca.domain.entities.Match
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
) {
    fun asBet(): Bet {
        val homeTeam = Team(this.homeTeamId, this.homeTeamName, this.homeTeamLogo)
        val awayTeam = Team(this.awayTeamId, this.awayTeamName, this.awayTeamLogo)
        if (this.date.isBefore(LocalDate.now())) {
            val match = Match(
                this.matchId,
                homeTeam,
                awayTeam,
                this.date,
                null,
                null,
                null
            )
            val betStatus = if (this.goalsAway == null || this.goalsHome == null) {
                BetStatus.Pending
            } else {
                BetStatus.Done
            }
            return Bet(match, betStatus, this.goalsHome, this.goalsAway)
        } else {
            val match = Match(
                this.matchId,
                homeTeam,
                awayTeam,
                this.date,
                this.goalsHome,
                this.goalsAway,
                null
            )
            return Bet(match)
        }
    }
}
