package com.marklynch.weather.livedata.sharedpreferences

import android.content.Context

class BooleanSharedPreferencesLiveData(
    context: Context,
    sharedPreferencesKey: String
) : SharedPreferencesLiveData<Boolean>(context, sharedPreferencesKey) {
    override fun setLiveDataValue(sharedPreferencesKey: String) {
        postValue(sharedPreferences.getBoolean(sharedPreferencesKey,false))
    }
}