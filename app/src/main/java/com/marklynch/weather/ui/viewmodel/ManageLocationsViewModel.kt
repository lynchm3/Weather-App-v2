package com.marklynch.weather.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.marklynch.weather.model.ManualLocation
import com.marklynch.weather.repository.db.ManualLocationRepository
import com.marklynch.weather.repository.location.LocationRepository
import com.sucho.placepicker.AddressData
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

open class ManageLocationsViewModel(application: Application) : AndroidViewModel(application),
    KoinComponent {

    //Location
    val locationRepository: LocationRepository by inject()
    //    val manualLocationRepository = ManualLocationRepository(application)
    private val manualLocationRepository: ManualLocationRepository by inject()
    val manualLocationLiveData = manualLocationRepository.manualLocationLiveData

    fun getLocationInformation() = locationRepository.value

    fun addManualLocation(addressData: AddressData?) {
        if (addressData != null)
            manualLocationRepository.insert(addressData)
    }

    fun deleteManualLocation(manualLocation: ManualLocation) {
        manualLocationRepository.delete(manualLocation)
    }

    fun renameManualLocation(manualLocation: ManualLocation, displayName: String) {
        manualLocationRepository.rename(manualLocation, displayName)
    }
}