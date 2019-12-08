package com.marklynch.weather.data.manuallocation

import androidx.lifecycle.LiveData
import androidx.room.*
import com.marklynch.weather.model.SearchedLocation

@Dao
interface SearchedLocationDAO {

    @Insert
    fun insert(searchedLocation: SearchedLocation): Long

    @Update
    fun update(searchedLocation: SearchedLocation)

    @Delete
    fun delete(searchedLocation: SearchedLocation)

    @Query("SELECT * FROM ManualLocation ORDER BY displayName ASC")
    fun getManualLocationLiveData(): LiveData<List<SearchedLocation>>

    @Query("SELECT * FROM ManualLocation WHERE id = :id")
    fun getManualLocationById(id: Long): SearchedLocation
}