package com.marklynch.weather.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.marklynch.weather.model.db.SearchedLocation
import com.marklynch.weather.data.searchedlocation.SearchedLocationDAO

@Database(entities = [SearchedLocation::class], version = 1)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun getSearchedLocationDao(): SearchedLocationDAO
}