package com.example.penca.network.entities

import com.example.penca.domain.entities.BetResult
import com.example.penca.domain.entities.BetStatus
import com.example.penca.network.entities.NetworkMatchEvent
import com.squareup.moshi.Json
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

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