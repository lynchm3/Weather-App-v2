package com.marklynch.weather.livedata.sharedpreferences

import android.content.Context

class IntSharedPreferencesLiveData(
    context: Context,
    sharedPreferencesKey: String
) : SharedPreferencesLiveData<Int>(context, sharedPreferencesKey) {
    override fun setLiveDataValue(sharedPreferencesKey: String) {
        value = sharedPreferences.getInt(sharedPreferencesKey,0)
    }
}