package com.marklynch.weather.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.marklynch.weather.data.manuallocation.ManualLocation
import com.marklynch.weather.livedata.db.ManualLocationRepository
import com.marklynch.weather.livedata.location.LocationLiveData
import com.sucho.placepicker.AddressData
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

open class ManageLocationsViewModel(application: Application) : AndroidViewModel(application), KoinComponent {

    //Location
    val locationLiveData: LocationLiveData by inject()
//    val manualLocationRepository = ManualLocationRepository(application)
    val manualLocationRepository:ManualLocationRepository by inject()
    val manualLocationLiveData = manualLocationRepository.manualLocationLiveData

    fun getLocationInformation() = locationLiveData.value

    fun addManualLocation(addressData: AddressData?) {
        if (addressData != null)
            manualLocationRepository.insert(addressData)
    }

    fun deleteManualLocation(manualLocation: ManualLocation) {
        manualLocationRepository.delete(manualLocation)
    }

    fun renameManualLocation(manualLocation: ManualLocation, displayName:String) {
        manualLocationRepository.rename(manualLocation, displayName)
    }
}