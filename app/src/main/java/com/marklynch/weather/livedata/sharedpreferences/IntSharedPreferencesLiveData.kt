package com.marklynch.weather.livedata.sharedpreferences

import android.content.Context

class IntSharedPreferencesLiveData(
    sharedPreferencesKey: String
) : SharedPreferencesLiveData<Int>(sharedPreferencesKey) {
    override fun setLiveDataValue(sharedPreferencesKey: String) {
        postValue(sharedPreferences.getInt(sharedPreferencesKey,0))
    }
}