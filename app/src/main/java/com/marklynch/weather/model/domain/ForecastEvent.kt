package com.marklynch.weather.model.domain

data class ForecastEvent(
    val dayAndTime: String,
    val description: String,
    val icon: String,
    val temperature: Double
)