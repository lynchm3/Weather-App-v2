package com.marklynch.weather.livedata.db

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import com.marklynch.weather.data.WeatherDatabase
import com.marklynch.weather.data.manuallocation.ManualLocation
import com.marklynch.weather.data.manuallocation.ManualLocationDAO
import com.marklynch.weather.sharedpreferences.SHARED_PREFERENCES_CURRENT_LOCATION_ID
import com.sucho.placepicker.AddressData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.standalone.KoinComponent
import org.koin.standalone.get
import org.koin.standalone.inject

class ManualLocationRepository(context: Context) : KoinComponent {

    private val db: WeatherDatabase by inject()
    private val manualLocationDAO: ManualLocationDAO?
    val manualLocationLiveData: LiveData<List<ManualLocation>>?

    init {
        manualLocationDAO = db.getManualLocationDao()
        manualLocationLiveData = manualLocationDAO.getManualLocationLiveData()
    }

    fun insert(addressData: AddressData) {
        GlobalScope.launch {
            val newId = db.getManualLocationDao().insert(
                ManualLocation(
                    0,
                    addressData.addressList?.get(0)?.adminArea ?: "",
                    addressData.latitude,
                    addressData.longitude
                )
            )

            get<SharedPreferences.Editor>().putLong(SHARED_PREFERENCES_CURRENT_LOCATION_ID, newId).apply()
        }
    }

    fun delete(manualLocation: ManualLocation) {
        GlobalScope.launch {
            manualLocationDAO?.delete(manualLocation)
        }
    }

    fun rename(manualLocation: ManualLocation, displayName: String) {
        GlobalScope.launch {
            manualLocationDAO?.update(
                ManualLocation(
                    manualLocation.id,
                    displayName,
                    manualLocation.latitude,
                    manualLocation.longitude
                )
            )
        }
    }
}
