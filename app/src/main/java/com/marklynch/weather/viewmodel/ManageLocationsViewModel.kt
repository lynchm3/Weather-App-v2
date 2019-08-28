package com.marklynch.weather.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.marklynch.weather.data.ManualLocation
import com.marklynch.weather.livedata.db.ManualLocationRepository
import com.sucho.placepicker.AddressData
import org.koin.standalone.KoinComponent

open class ManageLocationsViewModel(application: Application) : AndroidViewModel(application), KoinComponent {

    val manualLocationRepository = ManualLocationRepository(application)
    val manualLocationLiveData = manualLocationRepository.manualLocationLiveData

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