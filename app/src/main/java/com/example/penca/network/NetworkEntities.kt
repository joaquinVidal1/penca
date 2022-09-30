package com.example.penca.network

import com.squareup.moshi.Json

data class ErrorResponseForLogIng(
    @Json(name = "message")
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


sealed class BaseProfileResponse(
    val networkUser: NetworkUser?,
    val message: String?
) {
    class Success(networkUser: NetworkUser) :
        BaseProfileResponse(networkUser, null)

    class ErrorResponse(message: String?) :
        BaseProfileResponse(null, message)
}