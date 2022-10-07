package com.example.penca.network.entities

import com.example.penca.database.DBMatch
import com.example.penca.domain.entities.Match
import com.example.penca.domain.entities.Team
import com.squareup.moshi.Json
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

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

    fun getLocalDate(date: String): LocalDate {
        val inputFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
        return LocalDate.parse(date, inputFormatter)
    }

    fun asDBMatch(): DBMatch = DBMatch(
        this.matchId,
        this.homeTeamId,
        this.homeTeamName,
        this.homeTeamLogo,
        this.awayTeamId,
        this.awayTeamName,
        this.awayTeamLogo,
        this.getLocalDate(this.date),
        this.homeTeamGoals,
        this.awayTeamGoals,
        this.predictedHomeGoals,
        this.predictedAwayGoals
    )
}