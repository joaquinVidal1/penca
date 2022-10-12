package com.example.penca.network.entities

import com.squareup.moshi.Json

data class AuthenticationBody(
    @Json(name = "email")
    val email: String,

    @Json(name = "password")
    val password: String
)