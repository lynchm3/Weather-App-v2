package com.marklynch.weather.repository.sharedpreferences.longsharedpreference

import android.content.SharedPreferences
import com.marklynch.weather.repository.sharedpreferences.SharedPreferencesLiveData
import com.marklynch.weather.utils.SHARED_PREFERENCES_CURRENT_LOCATION_ID
import org.koin.standalone.KoinComponent
import org.koin.standalone.get

open class LongSharedPreferencesLiveData(
    sharedPreferencesKey: String
) : SharedPreferencesLiveData<Long>(sharedPreferencesKey), KoinComponent {

    override fun setLiveDataValue(sharedPreferencesKey: String) {
        postValue(sharedPreferences.getLong(sharedPreferencesKey, 0))
    }

    fun setSharedPreference(value: Long) {
        get<SharedPreferences.Editor>().putLong(sharedPreferencesKey, value).apply()
    }
}

class CurrentLocationIdSharedPreferenceLiveData : LongSharedPreferencesLiveData(
    SHARED_PREFERENCES_CURRENT_LOCATION_ID
)