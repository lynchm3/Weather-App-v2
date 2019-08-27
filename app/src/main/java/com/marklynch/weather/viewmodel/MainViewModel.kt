package com.marklynch.weather.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.marklynch.weather.data.ManualLocation
import com.marklynch.weather.livedata.db.ManualLocationRepository
import com.marklynch.weather.livedata.location.LocationLiveData
import com.marklynch.weather.livedata.network.NetworkInfoLiveData
import com.marklynch.weather.livedata.sharedpreferences.BooleanSharedPreferencesLiveData
import com.marklynch.weather.livedata.sharedpreferences.LongSharedPreferencesLiveData
import com.marklynch.weather.livedata.weather.WeatherLiveData
import com.marklynch.weather.sharedpreferences.SHARED_PREFERENCES_LOCATION_ID
import com.marklynch.weather.sharedpreferences.SHARED_PREFERENCES_USE_24_HR_CLOCK
import com.marklynch.weather.sharedpreferences.SHARED_PREFERENCES_USE_CELSIUS
import com.marklynch.weather.sharedpreferences.SHARED_PREFERENCES_USE_KM
import com.sucho.placepicker.AddressData
import org.koin.core.parameter.parametersOf
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

open class MainViewModel(application: Application) : AndroidViewModel(application), KoinComponent {

    //Location
    val locationLiveData: LocationLiveData by inject()

    //Weather
    val weatherLiveData: WeatherLiveData by inject()

    //Internet Connection
    val networkInfoLiveData: NetworkInfoLiveData by inject()

    //Shared Preference for setting whether to use degrees C or F
    val useCelsiusSharedPreferencesLiveData: BooleanSharedPreferencesLiveData by inject {
        parametersOf(
            SHARED_PREFERENCES_USE_CELSIUS
        )
    }

    //Shared Preference for setting whether to use km or mi
    val useKmSharedPreferencesLiveData: BooleanSharedPreferencesLiveData by inject {
        parametersOf(
            SHARED_PREFERENCES_USE_KM
        )
    }

    //Shared Preference for setting whether to use 24 or 12 hr clock
    val use24hrClockSharedPreferencesLiveData: BooleanSharedPreferencesLiveData by inject {
        parametersOf(
            SHARED_PREFERENCES_USE_24_HR_CLOCK
        )
    }

    val selectedLocationIdSharedPreferencesLiveData: LongSharedPreferencesLiveData by inject {
        parametersOf(
            SHARED_PREFERENCES_LOCATION_ID
        )
    }

    val manualLocationRepository = ManualLocationRepository(application)
    val manualLocationLiveData = manualLocationRepository.manualLocationLiveData

    fun getLocationInformation() = locationLiveData.value
    fun getWeather() = weatherLiveData.value
    fun isUseCelsius() = useCelsiusSharedPreferencesLiveData.value
    fun isUseKm() = useKmSharedPreferencesLiveData.value
    fun isUse24hrClock() = use24hrClockSharedPreferencesLiveData.value
    fun getSelectedLocationId() = selectedLocationIdSharedPreferencesLiveData.value

    fun setUseCelsius(useCelsius: Boolean) {
        useCelsiusSharedPreferencesLiveData.setSharedPreference(useCelsius)
    }

    fun setUseKm(useKm: Boolean) {
        useKmSharedPreferencesLiveData.setSharedPreference(useKm)
    }

    fun setUse24hrClock(use24hrClock: Boolean) {
        use24hrClockSharedPreferencesLiveData.setSharedPreference(use24hrClock)
    }

    fun setSelectedLocationId(selectedLocationId: Long) {
        selectedLocationIdSharedPreferencesLiveData.setSharedPreference(selectedLocationId)
    }

    fun fetchLocation() {
        locationLiveData.fetchLocation()
    }

    fun fetchWeather(manualLocation:ManualLocation?) {
        if(manualLocation == null) {
            val lat = getLocationInformation()?.location?.latitude
            val lon = getLocationInformation()?.location?.longitude
            if (lat != null && lon != null)
                weatherLiveData.fetchWeather(lat, lon)
        }
        else
        {
            weatherLiveData.fetchWeather(manualLocation.latitude, manualLocation.longitude)
        }
    }

    fun addManualLocation(addressData: AddressData?) {
        if(addressData != null)
            manualLocationRepository.insert(addressData)
    }
}