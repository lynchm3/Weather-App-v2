package com.marklynch.weather.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ManualLocationDAO {

    @Insert
    fun insert(manualLocation: ManualLocation):Long

    @Update
    fun update(manualLocation: ManualLocation)

    @Delete
    fun delete(manualLocation: ManualLocation)

    @Query("SELECT * FROM ManualLocation ORDER BY displayName ASC")
    fun getManualLocationLiveData(): LiveData<List<ManualLocation>>

    @Query("SELECT * FROM ManualLocation WHERE id = :id")
    fun getManualLocationById(id: Long): ManualLocation
}