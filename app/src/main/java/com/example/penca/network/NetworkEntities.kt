package com.example.penca.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

data class ErrorResponseForLogIng(
    val message: String?
) {}

data class NetworkUser(
    @Json(name = "userId")
    val userId: Int?,

    @Json(name = "email")
    val email: String?,

    @Json(name = "token")
    val token: String?
)
