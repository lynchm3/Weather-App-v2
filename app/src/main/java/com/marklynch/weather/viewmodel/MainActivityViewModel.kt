package com.marklynch.weather.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.marklynch.weather.livedata.location.LocationLiveData
import com.marklynch.weather.livedata.sharedpreferences.IntSharedPreferencesLiveData
import com.marklynch.weather.livedata.util.CurrentTimeLiveData
import com.marklynch.weather.livedata.weather.WeatherLiveData
import com.marklynch.weather.livedata.webresource.RawWebResourceLiveData
import com.marklynch.weather.view.MainActivity

class MainActivityViewModel(application: Application) : BaseActivityViewModel(application) {
    val liveDataFab = MutableLiveData<String>()

    //Time
    val currentTimeLiveData = CurrentTimeLiveData()

    //Raw web resource
    val rawWebResourceLiveData = RawWebResourceLiveData()

    //Location
    val locationLiveData = LocationLiveData(application)

    //Weather
    val weatherLiveData = WeatherLiveData()

    //Shared pref Int
    val intSharedPreferencesLiveData = IntSharedPreferencesLiveData(application, MainActivity.companion.testSharedPref)
}