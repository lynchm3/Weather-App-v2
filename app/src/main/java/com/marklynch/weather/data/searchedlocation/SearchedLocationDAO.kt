package com.marklynch.weather.data.searchedlocation

import androidx.room.*
import com.marklynch.weather.model.db.SearchedLocation

@Dao
interface SearchedLocationDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(searchedLocation: SearchedLocation)

    @Update
    fun update(searchedLocation: SearchedLocation)

    @Delete
    fun delete(searchedLocation: SearchedLocation)

    @Query("SELECT * FROM SearchedLocation ORDER BY displayName ASC")
    fun getSearchedLocations(): List<SearchedLocation>

    @Query("SELECT * FROM SearchedLocation WHERE id = :id")
    fun getSearchedLocationById(id: String): SearchedLocation
}