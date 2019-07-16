package com.marklynch.weather.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.marklynch.weather.livedata.location.LocationLiveData
import com.marklynch.weather.livedata.sharedpreferences.BooleanSharedPreferencesLiveData
import com.marklynch.weather.livedata.sharedpreferences.IntSharedPreferencesLiveData
import com.marklynch.weather.livedata.util.CurrentTimeLiveData
import com.marklynch.weather.livedata.weather.WeatherLiveData
import com.marklynch.weather.livedata.webresource.RawWebResourceLiveData
import com.marklynch.weather.sharedpreferences.SHARED_PREFERENCES_USE_CELCIUS
import com.marklynch.weather.sharedpreferences.SHARED_PREFERENCES_USE_KM
import com.marklynch.weather.view.MainActivity

class MainActivityViewModel(application: Application) : BaseActivityViewModel(application) {

    //Location
    val locationLiveData = LocationLiveData(application)

    //Weather
    val weatherLiveData = WeatherLiveData()

    //Shared Preference for setting whether to use degrees C or F
    val useCelciusSharedPreferencesLiveData = BooleanSharedPreferencesLiveData(application, SHARED_PREFERENCES_USE_CELCIUS)

    //Shared Preference for setting whether to use degrees km or mi
    val useKmSharedPreferencesLiveData = BooleanSharedPreferencesLiveData(application, SHARED_PREFERENCES_USE_KM)
}