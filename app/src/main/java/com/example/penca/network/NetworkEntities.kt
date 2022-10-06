package com.example.penca.network

import com.example.penca.domain.entities.*
import com.squareup.moshi.Json
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

data class ErrorResponseForLogIng(
    val message: String?
)

data class SeeDetailsBet(
    @Json(name = "matchId")
    val matchId: Int,

    @Json(name = "date")
    val date: String,

    @Json(name = "homeTeamName")
    val homeTeamName: String,

    @Json(name = "homeTeamLogo")
    val homeTeamLogo: String,

    @Json(name = "homeTeamGoals")
    val homeTeamGoals: Int?,

    @Json(name = "awayTeamName")
    val awayTeamName: String,

    @Json(name = "awayTeamLogo")
    val awayTeamLogo: String,

    @Json(name = "awayTeamGoals")
    val awayTeamGoals: Int?,

    @Json(name = "incidences")
    val events: List<NetworkMatchEvent>,

    @Json(name = "predictionStatus")
    val predictionStatus: String,

    @Json(name = "homeTeamPrediction")
    val homeTeamPrediction: Int?,

    @Json(name = "awayTeamPrediction")
    val awayTeamPrediction: Int?
) {
    val result =
        if ((homeTeamGoals == homeTeamPrediction) && (awayTeamGoals == awayTeamPrediction)) {
            BetResult.Right
        } else {
            BetResult.Wrong
        }

    fun getStatusFromString(): BetStatus {
        return if (predictionStatus == "not_predicted") {
            BetStatus.Pending
        } else {
            BetStatus.Done
        }
    }

    fun getLocalDate(): LocalDate {
        val inputFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
        return LocalDate.parse(date, inputFormatter)
    }
}

data class NetworkMatchEvent(

    @Json(name = "minute")
    val minute: Int,

    @Json(name = "side")
    val side: String,

    @Json(name = "event")
    val event: String,

    @Json(name = "playerName")
    val playerName: String
) {
    private fun getMatchTeamFromString(side: String): MatchTeams {
        return if (side == "home") {
            MatchTeams.Home
        } else {
            MatchTeams.Away
        }

    }

    private fun getMatchEventKindFromString(event: String): MatchEventKind {
        return when (event) {
            "goal" -> MatchEventKind.Goal
            "red card" -> MatchEventKind.RedCard
            "yellow card" -> MatchEventKind.YellowCard
            else -> MatchEventKind.Goal
        }
    }

    fun getMatchEventFromNetworkEvent() =
        MatchEvent(
            this.minute,
            this.playerName,
            getMatchEventKindFromString(this.event),
            getMatchTeamFromString(this.side)
        )
}

data class NetworkUser(
    @Json(name = "userId")
    val userId: Int,

    @Json(name = "email")
    val email: String,

    @Json(name = "token")
    var token: String
)

data class NetworkMatch(
    @Json(name = "matchId")
    val matchId: Int,

    @Json(name = "date")
    val date: String,

    @Json(name = "homeTeamId")
    val homeTeamId: Int,

    @Json(name = "homeTeamName")
    val homeTeamName: String,

    @Json(name = "homeTeamLogo")
    val homeTeamLogo: String,

    @Json(name = "awayTeamId")
    val awayTeamId: Int,

    @Json(name = "awayTeamName")
    val awayTeamName: String,

    @Json(name = "awayTeamLogo")
    val awayTeamLogo: String,

    @Json(name = "status")
    val status: String,

    @Json(name = "homeTeamGoals")
    val homeTeamGoals: Int?,

    @Json(name = "awayTeamGoals")
    val awayTeamGoals: Int?,

    @Json(name = "predictedHomeGoals")
    val predictedHomeGoals: Int?,

    @Json(name = "predictedAwayGoals")
    val predictedAwayGoals: Int?
) {
    fun asDomainMatch(): Match {
        val homeTeam = Team(homeTeamId, homeTeamName, homeTeamLogo)
        val awaTeam = Team(awayTeamId, awayTeamName, awayTeamLogo)
        //val matchStatus = if (status == "pending") MatchStatus.Pending else MatchStatus.Played
        val localDate = getLocalDate(date)
        return Match(matchId, homeTeam, awaTeam, localDate, homeTeamGoals, awayTeamGoals, null)
    }

    private fun getLocalDate(date: String): LocalDate {
        val inputFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
        return LocalDate.parse(date, inputFormatter)
    }
}

data class MatchesContainer(
    @Json(name = "matches")
    val matches: List<NetworkMatch>,
)

data class AuthenticationBody(
    @Json(name = "email")
    val email: String,

    @Json(name = "password")
    val password: String
)

data class BetBody(
    @Json(name = "homeGoals")
    val homeTeamGoals: Int,

    @Json(name = "awayGoals")
    val awayTeamGoals: Int
)

data class BetAnswer(
    @Json(name = "userId")
    val userId: Int,

    @Json(name = "matchId")
    val matchId: Int,

    @Json(name = "homeGoals")
    val homeGoals: Int,

    @Json(name = "awayGoals")
    val awayGoals: Int
)

