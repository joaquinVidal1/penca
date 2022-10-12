package com.example.penca.network.entities

import com.squareup.moshi.Json

data class BannersResponse(
    @Json(name = "bannerURLs")
    val bannersUrls: List<String>
) {
}