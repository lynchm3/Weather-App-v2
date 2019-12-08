package com.marklynch.weather.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.marklynch.weather.model.SearchedLocation
import com.marklynch.weather.data.manuallocation.SearchedLocationDAO

@Database(entities = [SearchedLocation::class], version = 1)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun getManualLocationDao(): SearchedLocationDAO
}