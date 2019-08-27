package com.marklynch.weather.livedata.db

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import com.marklynch.weather.data.ManualLocation
import com.marklynch.weather.data.ManualLocationDAO
import com.marklynch.weather.data.WeatherDatabase
import com.marklynch.weather.sharedpreferences.SHARED_PREFERENCES_LOCATION_ID
import com.sucho.placepicker.AddressData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.koin.standalone.KoinComponent
import org.koin.standalone.get

class ManualLocationRepository(context: Context) : KoinComponent {

    private val db: WeatherDatabase? = WeatherDatabase.getDatabase(context)
    private val manualLocationDAO: ManualLocationDAO?
    val manualLocationLiveData: LiveData<List<ManualLocation>>?

    init {
        manualLocationDAO = db?.manualLocationDao()
        manualLocationLiveData = manualLocationDAO?.getManualLocationLiveData()
    }

    fun insert(addressData: AddressData) {
        GlobalScope.async {
            val newId = db?.manualLocationDao()?.insertManualLocation(
                ManualLocation(
                    0,
                    addressData?.addressList?.get(0)?.adminArea ?: "",
                    addressData?.latitude,
                    addressData?.longitude
                )
            )

            if (newId != null)
                get<SharedPreferences.Editor>().putLong(SHARED_PREFERENCES_LOCATION_ID, newId).apply()

            Log.i("newId", "newId = $newId")
        }
    }

    fun delete(manualLocation: ManualLocation)
    {
        GlobalScope.launch{
            manualLocationDAO?.delete(manualLocation)
        }
    }
}
