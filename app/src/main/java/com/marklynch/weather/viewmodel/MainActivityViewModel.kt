package com.marklynch.weather.viewmodel

import android.app.Application
import com.marklynch.weather.livedata.location.LocationLiveData
import com.marklynch.weather.livedata.network.NetworkInfoLiveData
import com.marklynch.weather.livedata.sharedpreferences.BooleanSharedPreferencesLiveData
import com.marklynch.weather.livedata.weather.WeatherLiveData
import com.marklynch.weather.sharedpreferences.SHARED_PREFERENCES_USE_CELCIUS
import com.marklynch.weather.sharedpreferences.SHARED_PREFERENCES_USE_KM

class MainActivityViewModel(application: Application) : BaseActivityViewModel(application) {

    //Location
    val locationLiveData = LocationLiveData(application)

    //Weather
    val weatherLiveData = WeatherLiveData()

    //Internet Connection
    val networkInfoLiveData = NetworkInfoLiveData(application)

    //Shared Preference for setting whether to use degrees C or F
    val useCelciusSharedPreferencesLiveData = BooleanSharedPreferencesLiveData(application, SHARED_PREFERENCES_USE_CELCIUS)

    //Shared Preference for setting whether to use degrees km or mi
    val useKmSharedPreferencesLiveData = BooleanSharedPreferencesLiveData(application, SHARED_PREFERENCES_USE_KM)
}