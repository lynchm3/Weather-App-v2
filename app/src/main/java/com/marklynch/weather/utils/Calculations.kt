package com.marklynch.weather.utils

fun farenheitToCelsius(fahrenheit: Double?) = if(fahrenheit == null) 0.0 else (fahrenheit - 32) * 5 / 9
fun kelvinToCelsius(kelvin: Double?) = if(kelvin == null) 0.0 else kelvin - 273.15
fun kelvinToFahrenheit(kelvin: Double?) = if(kelvin == null) 0.0 else kelvin * 9/5 - 459.67

fun metresPerSecondToKmPerHour(metresPerSecond: Double) = (metresPerSecond * 3.6)
fun metresPerSecondToMilesPerHour(metresPerSecond: Double) = (metresPerSecond * 2.23694)
