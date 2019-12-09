package com.marklynch.weather.model.response


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Wind(
    @Json(name = "deg")
    val deg: Double,
    @Json(name = "speed")
    val speed: Double
)