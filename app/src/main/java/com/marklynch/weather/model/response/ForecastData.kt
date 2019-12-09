package com.marklynch.weather.model.response


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ForecastData(
    @Json(name = "clouds")
    val clouds: Clouds,
    @Json(name = "dt")
    val dt: Long,
    @Json(name = "dt_txt")
    val dtTxt: String,
    @Json(name = "main")
    val weatherInfo: WeatherInfo,
    @Json(name = "sys")
    val sys: Sys,
    @Json(name = "weather")
    val weather: List<Weather>,
    @Json(name = "wind")
    val wind: Wind
)