package com.marklynch.weather.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ManualLocation::class], version = 1)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun manualLocationDao(): ManualLocationDAO
    companion object {
        private var INSTANCE: WeatherDatabase? = null
        fun getDatabase(context: Context): WeatherDatabase? {
            if (INSTANCE == null) {
                synchronized(WeatherDatabase::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        WeatherDatabase::class.java, "weather.db"
                    ).build()
                }
            }
            return INSTANCE
        }
    }
}