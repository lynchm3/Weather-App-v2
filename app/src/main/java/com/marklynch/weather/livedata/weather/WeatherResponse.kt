package com.marklynch.weather.livedata.weather

import com.google.gson.annotations.SerializedName

import java.util.ArrayList

class WeatherResponse {

    @SerializedName("coord")
    var coord: Coord? = null
    @SerializedName("sys")
    var sys: Sys? = null
    @SerializedName("weather")
    var weather: List<Weather> = listOf()
    @SerializedName("main")
    var main: Main? = null
    @SerializedName("wind")
    var wind: Wind? = null
    @SerializedName("rain")
    var rain: Rain? = null
    @SerializedName("clouds")
    var clouds: Clouds? = null
    @SerializedName("dt")
    var dt = 0.0
    @SerializedName("id")
    var id: Int = 0
    @SerializedName("name")
    var name: String? = null
    @SerializedName("cod")
    var cod = 0.0
}

class Weather {
    @SerializedName("id")
    var id: Int = 0
    @SerializedName("main")
    var main: String? = null
    @SerializedName("description")
    var description: String? = null
    @SerializedName("icon")
    var icon: String? = null
}

class Clouds {
    @SerializedName("all")
    var all = 0.0
}

class Rain {
    @SerializedName("3h")
    var h3 = 0.0
}

class Wind {
    @SerializedName("speed")
    var speed = 0.0
    @SerializedName("deg")
    var deg = 0.0
}

class Main {
    @SerializedName("temp")
    var temp = 0.0
    @SerializedName("humidity")
    var humidity = 0.0
    @SerializedName("pressure")
    var pressure = 0.0
    @SerializedName("temp_min")
    var temp_min = 0.0
    @SerializedName("temp_max")
    var temp_max = 0.0
}

class Sys {
    @SerializedName("country")
    var country: String? = null
    @SerializedName("sunrise")
    var sunrise: Long = 0
    @SerializedName("sunset")
    var sunset: Long = 0
}

class Coord {
    @SerializedName("lon")
    var lon = 0.0
    @SerializedName("lat")
    var lat = 0.0
}