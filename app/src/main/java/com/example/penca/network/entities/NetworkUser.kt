package com.example.penca.network.entities

import com.squareup.moshi.Json

data class NetworkUser(
    @Json(name = "userId")
    val userId: Int,

    @Json(name = "email")
    val email: String,

    @Json(name = "token")
    var token: String
)