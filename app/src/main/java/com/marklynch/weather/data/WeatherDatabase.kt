package com.marklynch.weather.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.marklynch.weather.data.manuallocation.ManualLocation
import com.marklynch.weather.data.manuallocation.ManualLocationDAO

@Database(entities = [ManualLocation::class], version = 1)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun getManualLocationDao(): ManualLocationDAO
}