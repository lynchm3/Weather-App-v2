package com.marklynch.weather.model.response


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Weather(
    @Json(name = "description")
    val description: String,
    @Json(name = "icon")
    val icon: String
)