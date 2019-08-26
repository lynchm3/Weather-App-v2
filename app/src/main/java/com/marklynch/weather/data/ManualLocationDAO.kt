package com.marklynch.weather.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ManualLocationDAO {

    @Query("SELECT * FROM ManualLocation ORDER BY displayName ASC")
    fun getManualLocationLiveData(): LiveData<List<ManualLocation>>

    @Insert
    fun insertManualLocation(manualLocation: ManualLocation):Long

    @Update
    fun updateManualLocation(manualLocation: ManualLocation)

    @Delete
    fun delete(manualLocation: ManualLocation)

    @Query("SELECT * FROM ManualLocation WHERE id = :id")
    fun loadManualLocationById(id: Int): ManualLocation
}