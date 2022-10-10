package com.example.penca.network.entities

import com.squareup.moshi.Json

data class BetAnswer(
    @Json(name = "userId")
    val userId: Int,

    @Json(name = "matchId")
    val matchId: Int,

    @Json(name = "homeGoals")
    val homeGoals: Int,

    @Json(name = "awayGoals")
    val awayGoals: Int,

    @Json(name = "message")
    val message: String?
)