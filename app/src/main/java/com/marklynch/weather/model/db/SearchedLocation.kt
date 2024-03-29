package com.marklynch.weather.model.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


val currentLocation = SearchedLocation(
    "0",
    "Current Location"
)

@Entity(tableName = "searchedLocation")
data class SearchedLocation(
    @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "displayName") val displayName: String
)