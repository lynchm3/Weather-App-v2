package com.marklynch.weather.utils

import android.content.Context
import kotlin.math.roundToInt

fun kelvinToCelsius(kelvin: Double?) = if (kelvin == null) 0.0 else kelvin - 273.15
fun kelvinToFahrenheit(kelvin: Double?) = if (kelvin == null) 0.0 else kelvin * 9 / 5 - 459.67

fun metresPerSecondToKmPerHour(metresPerSecond: Double) = (metresPerSecond * 3.6)
fun metresPerSecondToMilesPerHour(metresPerSecond: Double) = (metresPerSecond * 2.23694)

fun directionInDegreesToCardinalDirection(directionInDegrees: Double): String {
    val directions = arrayOf("N", "NE", "E", "SE", "S", "SW", "W", "NW", "N")
    return directions[(directionInDegrees % 360 / 45).roundToInt()]
}

fun dpToPixels(dp:Int, context: Context):Int{
    val scale = context.resources.displayMetrics.density
    return (dp * scale + 0.5f).toInt()
}
