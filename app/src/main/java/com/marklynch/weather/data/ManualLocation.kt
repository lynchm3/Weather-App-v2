package com.marklynch.weather.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "manualLocation")
data class ManualLocation(
    @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "displayName") val displayName: String,
    @ColumnInfo(name = "latitude") val latitude: Double,
    @ColumnInfo(name = "longitude") val longitude: Double
)
