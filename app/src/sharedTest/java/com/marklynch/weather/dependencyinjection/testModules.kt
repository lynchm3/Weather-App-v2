package com.marklynch.weather.dependencyinjection

import androidx.room.Room
import com.marklynch.weather.data.WeatherDatabase
import com.marklynch.weather.generateGetWeatherResponse
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.koin.dsl.module.module


val testWeatherDatabase = module(override = true) {
    single {
        Room.inMemoryDatabaseBuilder(
            get(), WeatherDatabase::class.java
        ).build()
    }
}

//var testWebServer = MockWebServer()
lateinit var testWebServer:MockWebServer
val testModuleHttpUrl = module(override = true) {
    factory { (_: String) ->
        testWebServer.url("")
    }
}