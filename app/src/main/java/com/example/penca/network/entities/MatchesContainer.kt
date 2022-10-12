package com.example.penca.network.entities

import com.squareup.moshi.Json

data class MatchesContainer(
    @Json(name = "matches")
    val matches: List<NetworkMatch>,
)



