package com.marklynch.weather.livedata.sharedpreferences

import android.content.SharedPreferences
import org.koin.standalone.KoinComponent
import org.koin.standalone.get

class LongSharedPreferencesLiveData(
    sharedPreferencesKey: String
) : SharedPreferencesLiveData<Long>(sharedPreferencesKey), KoinComponent {

    override fun setLiveDataValue(sharedPreferencesKey: String) {
        postValue(sharedPreferences.getLong(sharedPreferencesKey, 0))
    }

    fun setSharedPreference(value: Long) {
        get<SharedPreferences.Editor>().putLong(sharedPreferencesKey, value).apply()
    }
}