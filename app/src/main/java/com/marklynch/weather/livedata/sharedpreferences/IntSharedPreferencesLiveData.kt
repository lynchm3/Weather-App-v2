package com.marklynch.weather.livedata.sharedpreferences

class IntSharedPreferencesLiveData(
    sharedPreferencesKey: String
) : SharedPreferencesLiveData<Int>(sharedPreferencesKey) {
    override fun setLiveDataValue(sharedPreferencesKey: String) {
        postValue(sharedPreferences.getInt(sharedPreferencesKey, 0))
    }
}