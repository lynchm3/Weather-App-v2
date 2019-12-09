package com.marklynch.weather.model.response


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WeatherInfo(
    @Json(name = "grnd_level")
    val grndLevel: Int,
    @Json(name = "humidity")
    val humidity: Int,
    @Json(name = "pressure")
    val pressure: Int,
    @Json(name = "sea_level")
    val seaLevel: Int,
    @Json(name = "temp")
    val temperature: Double,
    @Json(name = "temp_kf")
    val tempKf: Double,
    @Json(name = "temp_max")
    val tempMaximum: Double,
    @Json(name = "temp_min")
    val temperatureMin: Double
)