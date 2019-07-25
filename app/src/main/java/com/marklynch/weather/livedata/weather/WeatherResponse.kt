package com.marklynch.weather.livedata.weather

import com.google.gson.annotations.SerializedName

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

    override fun toString(): String {
        return "WeatherResponse(coord=$coord, sys=$sys, weather=$weather, main=$main, wind=$wind, rain=$rain, clouds=$clouds, dt=$dt, id=$id, name=$name, cod=$cod)"
    }


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

    override fun toString(): String {
        return "Weather(id=$id, main=$main, description=$description, icon=$icon)"
    }
}

class Clouds {
    @SerializedName("all")
    var all = 0.0

    override fun toString(): String {
        return "Clouds(all=$all)"
    }
}

class Rain {
    @SerializedName("3h")
    var h3 = 0.0

    override fun toString(): String {
        return "Rain(h3=$h3)"
    }
}

class Wind {
    @SerializedName("speed")
    var speed = 0.0
    @SerializedName("deg")
    var deg = 0.0

    override fun toString(): String {
        return "Wind(speed=$speed, deg=$deg)"
    }
}

class Main {
    @SerializedName("temp")
    var temp = 0.0
    @SerializedName("humidity")
    var humidity = 0.0
    @SerializedName("pressure")
    var pressure = 0.0
    @SerializedName("temp_min")
    var tempMin = 0.0
    @SerializedName("temp_max")
    var tempMax = 0.0

    override fun toString(): String {
        return "Main(temp=$temp, humidity=$humidity, pressure=$pressure, tempMin=$tempMin, tempMax=$tempMax)"
    }
}

class Sys {
    @SerializedName("country")
    var country: String? = null
    @SerializedName("sunrise")
    var sunrise: Long = 0
    @SerializedName("sunset")
    var sunset: Long = 0

    override fun toString(): String {
        return "Sys(country=$country, sunrise=$sunrise, sunset=$sunset)"
    }
}

class Coord {
    @SerializedName("lon")
    var lon = 0.0
    @SerializedName("lat")
    var lat = 0.0

    override fun toString(): String {
        return "Coord(lon=$lon, lat=$lat)"
    }
}