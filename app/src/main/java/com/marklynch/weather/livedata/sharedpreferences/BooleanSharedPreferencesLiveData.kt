package com.marklynch.weather.livedata.sharedpreferences

import android.content.Context
import android.preference.PreferenceManager

class BooleanSharedPreferencesLiveData(
    context: Context,
    sharedPreferencesKey: String
) : SharedPreferencesLiveData<Boolean>(context, sharedPreferencesKey) {
    override fun setLiveDataValue(sharedPreferencesKey: String) {
        postValue(sharedPreferences.getBoolean(sharedPreferencesKey, false))
    }

    fun setSharedPreference(value: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
            .putBoolean(sharedPreferencesKey, value).apply()

    }
}