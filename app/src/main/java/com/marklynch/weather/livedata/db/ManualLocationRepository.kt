package com.marklynch.weather.livedata.db

import android.content.Context
import androidx.lifecycle.LiveData
import com.marklynch.weather.data.ManualLocation
import com.marklynch.weather.data.ManualLocationDAO
import com.marklynch.weather.data.WeatherDatabase
import com.sucho.placepicker.AddressData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.koin.standalone.KoinComponent

class ManualLocationRepository(context: Context) : KoinComponent {

    val db: WeatherDatabase?
    val manualLocationDAO: ManualLocationDAO?
    val manualLocationLiveData: LiveData<List<ManualLocation>>?

    init {
        db = WeatherDatabase.getDatabase(context)
        manualLocationDAO = db?.manualLocationDao()
        manualLocationLiveData = manualLocationDAO?.getManualLocationLiveData()
    }

    fun insert(addressData: AddressData) {
        GlobalScope.async {
            db?.manualLocationDao()?.insertManualLocation(
                ManualLocation(
                    null,
                    addressData?.addressList?.get(0)?.getAddressLine(0)?:"",
                    addressData?.latitude,
                    addressData?.longitude
                )
            )
        }
    }
}
