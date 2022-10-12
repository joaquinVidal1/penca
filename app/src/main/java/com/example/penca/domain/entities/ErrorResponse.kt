package com.example.penca.domain.entities

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ErrorResponse(
    val message: String
) {
}