package com.marklynch.weather.data

import android.app.Application
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.marklynch.weather.data.manuallocation.ManualLocation
import com.marklynch.weather.data.manuallocation.ManualLocationDAO
import org.koin.dsl.module.applicationContext

@Database(entities = [ManualLocation::class], version = 1)
abstract class WeatherDatabase : RoomDatabase() {

    abstract fun getManualLocationDao(): ManualLocationDAO
}