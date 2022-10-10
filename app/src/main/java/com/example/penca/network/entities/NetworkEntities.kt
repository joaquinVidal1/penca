package com.example.penca.network.entities

import com.example.penca.database.DBMatch
import com.example.penca.domain.entities.*
import com.squareup.moshi.Json
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

data class ErrorResponseForLogIng(
    val message: String?
)

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


