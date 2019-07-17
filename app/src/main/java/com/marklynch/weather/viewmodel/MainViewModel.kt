package com.marklynch.weather.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleObserver
import com.marklynch.weather.livedata.location.LocationLiveData
import com.marklynch.weather.livedata.network.NetworkInfoLiveData
import com.marklynch.weather.livedata.sharedpreferences.BooleanSharedPreferencesLiveData
import com.marklynch.weather.livedata.weather.WeatherLiveData
import com.marklynch.weather.sharedpreferences.SHARED_PREFERENCES_USE_CELSIUS
import com.marklynch.weather.sharedpreferences.SHARED_PREFERENCES_USE_KM
import org.koin.core.parameter.parametersOf
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class MainViewModel(application: Application) : AndroidViewModel(application), KoinComponent {

    //Location
    val locationLiveData: LocationLiveData by inject()
//    val locationLiveData: LocationLiveData = LocationLiveData(application)

    //Weather
    val weatherLiveData: WeatherLiveData by inject()
//    val weatherLiveData: WeatherLiveData = WeatherLiveData()

    //Internet Connection
    val networkInfoLiveData: NetworkInfoLiveData by inject()
//    val networkInfoLiveData: NetworkInfoLiveData = NetworkInfoLiveData(application)

    //Shared Preference for setting whether to use degrees C or F
    val useCelsiusSharedPreferencesLiveData: BooleanSharedPreferencesLiveData by inject {
        parametersOf(
            SHARED_PREFERENCES_USE_CELSIUS
        )
    }

    //Shared Preference for setting whether to use degrees km or mi
    val useKmSharedPreferencesLiveData: BooleanSharedPreferencesLiveData by inject {
        parametersOf(
            SHARED_PREFERENCES_USE_KM
        )
    }

    fun setUseCelsius(useCelsius: Boolean) {
        useCelsiusSharedPreferencesLiveData.setSharedPreference(useCelsius)
    }

    fun setUseKm(useKm: Boolean) {
        useKmSharedPreferencesLiveData.setSharedPreference(useKm)
    }
}