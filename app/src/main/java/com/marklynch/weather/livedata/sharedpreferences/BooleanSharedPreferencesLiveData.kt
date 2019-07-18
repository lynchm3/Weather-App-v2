package com.marklynch.weather.livedata.sharedpreferences

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import org.koin.standalone.KoinComponent
import org.koin.standalone.get

class BooleanSharedPreferencesLiveData(
    sharedPreferencesKey: String
) : SharedPreferencesLiveData<Boolean>(sharedPreferencesKey), KoinComponent {

    override fun setLiveDataValue(sharedPreferencesKey: String) {
        postValue(sharedPreferences.getBoolean(sharedPreferencesKey, false))
    }

    fun setSharedPreference(value: Boolean) {
        get<SharedPreferences.Editor>().putBoolean(sharedPreferencesKey, value).apply()
    }
}