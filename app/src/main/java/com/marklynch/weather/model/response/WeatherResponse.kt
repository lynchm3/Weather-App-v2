package com.marklynch.weather.model.response

import com.marklynch.weather.R
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class WeatherResponse {

    @Json(name = "coord")
    var coord: Coord? = null
    @Json(name = "sys")
    var sys: Sys? = null
    @Json(name = "weather")
    var weather: List<Weather> = listOf()
    @Json(name = "main")
    var main: Main? = null
    @Json(name = "wind")
    var wind: Wind? = null
    @Json(name = "rain")
    var rain: Rain? = null
    @Json(name = "clouds")
    var clouds: Clouds? = null
    @Json(name = "dt")
    var dt = 0.0
    @Json(name = "id")
    var id: Int = 0
    @Json(name = "name")
    var name: String? = null
    @Json(name = "cod")
    var cod = 0.0

    override fun toString(): String {
        return "WeatherResponse(coord=$coord, sys=$sys, weather=$weather, main=$main, wind=$wind, rain=$rain, clouds=$clouds, dt=$dt, id=$id, name=$name, cod=$cod)"
    }

    companion object {
        val mapWeatherCodeToDrawable: Map<String, Int> = mapOf(
            "01d" to R.drawable.weather01d,
            "01n" to R.drawable.weather01n,
            "02d" to R.drawable.weather02d,
            "02n" to R.drawable.weather02n,
            "03d" to R.drawable.weather03d,
            "03n" to R.drawable.weather03d,
            "04d" to R.drawable.weather04d,
            "04n" to R.drawable.weather04d,
            "09d" to R.drawable.weather09d,
            "09n" to R.drawable.weather09d,
            "10d" to R.drawable.weather10d,
            "10n" to R.drawable.weather10n,
            "11d" to R.drawable.weather11d,
            "11n" to R.drawable.weather11d,
            "13d" to R.drawable.weather13d,
            "13n" to R.drawable.weather13d,
            "50d" to R.drawable.weather50d,
            "50n" to R.drawable.weather50d
        )

    }


}

class Rain {
    @Json(name = "3h")
    var h3 = 0.0

    override fun toString(): String {
        return "Rain(h3=$h3)"
    }
}

class Main {
    @Json(name = "temp")
    var temp = 0.0
    @Json(name = "humidity")
    var humidity = 0.0
    @Json(name = "pressure")
    var pressure = 0.0
    @Json(name = "temp_min")
    var tempMin = 0.0
    @Json(name = "temp_max")
    var tempMax = 0.0

    override fun toString(): String {
        return "Main(temp=$temp, humidity=$humidity, pressure=$pressure, tempMin=$tempMin, tempMax=$tempMax)"
    }
}

class Coord {
    @Json(name = "lon")
    var lon = 0.0
    @Json(name = "lat")
    var lat = 0.0

    override fun toString(): String {
        return "Coord(lon=$lon, lat=$lat)"
    }
}