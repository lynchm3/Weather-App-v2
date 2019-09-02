package com.marklynch.weather.dependencyinjection

import androidx.room.Room
import com.marklynch.weather.data.WeatherDatabase
import org.koin.dsl.module.module


val testWeatherDatabase = module(override = true) {
    single {
        Room.inMemoryDatabaseBuilder(
            get(), WeatherDatabase::class.java
        ).build()
    }
}