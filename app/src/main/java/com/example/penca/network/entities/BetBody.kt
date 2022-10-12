package com.example.penca.network.entities

import com.squareup.moshi.Json

data class BetBody(
    @Json(name = "homeGoals")
    val homeTeamGoals: Int,

    @Json(name = "awayGoals")
    val awayTeamGoals: Int
)