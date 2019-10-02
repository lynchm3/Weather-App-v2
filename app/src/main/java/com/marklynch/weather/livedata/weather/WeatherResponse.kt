package com.marklynch.weather.livedata.weather

import com.squareup.moshi.Json

class WeatherResponse {

    @Json(name = "coord")
    var coord: Coord? = null
    @field:Json(name = "sys")
    var sys: Sys? = null
    @field:Json(name = "weather")
    var weather: List<Weather> = listOf()
    @field:Json(name = "main")
    var main: Main? = null
    @field:Json(name = "wind")
    var wind: Wind? = null
    @field:Json(name = "rain")
    var rain: Rain? = null
    @field:Json(name = "clouds")
    var clouds: Clouds? = null
    @field:Json(name = "dt")
    var dt = 0.0
    @field:Json(name = "id")
    var id: Int = 0
    @field:Json(name = "name")
    var name: String? = null
    @field:Json(name = "cod")
    var cod = 0.0

    override fun toString(): String {
        return "WeatherResponse(coord=$coord, sys=$sys, weather=$weather, main=$main, wind=$wind, rain=$rain, clouds=$clouds, dt=$dt, id=$id, name=$name, cod=$cod)"
    }


}

class Weather {
    @field:Json(name = "id")
    var id: Int = 0
    @field:Json(name = "main")
    var main: String? = null
    @field:Json(name = "description")
    var description: String? = null
    @field:Json(name = "icon")
    var icon: String? = null

    override fun toString(): String {
        return "Weather(id=$id, main=$main, description=$description, icon=$icon)"
    }
}

class Clouds {
    @field:Json(name = "all")
    var all = 0.0

    override fun toString(): String {
        return "Clouds(all=$all)"
    }
}

class Rain {
    @field:Json(name = "3h")
    var h3 = 0.0

    override fun toString(): String {
        return "Rain(h3=$h3)"
    }
}

class Wind {
    @field:Json(name = "speed")
    var speed = 0.0
    @field:Json(name = "deg")
    var deg = 0.0

    override fun toString(): String {
        return "Wind(speed=$speed, deg=$deg)"
    }
}

class Main {
    @field:Json(name = "temp")
    var temp = 0.0
    @field:Json(name = "humidity")
    var humidity = 0.0
    @field:Json(name = "pressure")
    var pressure = 0.0
    @field:Json(name = "temp_min")
    var tempMin = 0.0
    @field:Json(name = "temp_max")
    var tempMax = 0.0

    override fun toString(): String {
        return "Main(temp=$temp, humidity=$humidity, pressure=$pressure, tempMin=$tempMin, tempMax=$tempMax)"
    }
}

class Sys {
    @field:Json(name = "country")
    var country: String? = null
    @field:Json(name = "sunrise")
    var sunrise: Long = 0
    @field:Json(name = "sunset")
    var sunset: Long = 0

    override fun toString(): String {
        return "Sys(country=$country, sunrise=$sunrise, sunset=$sunset)"
    }
}

class Coord {
    @field:Json(name = "lon")
    var lon = 0.0
    @field:Json(name = "lat")
    var lat = 0.0

    override fun toString(): String {
        return "Coord(lon=$lon, lat=$lat)"
    }
}