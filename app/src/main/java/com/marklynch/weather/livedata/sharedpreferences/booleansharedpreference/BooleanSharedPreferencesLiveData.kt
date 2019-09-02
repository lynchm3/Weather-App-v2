package com.marklynch.weather.livedata.sharedpreferences.booleansharedpreference

import android.content.SharedPreferences
import com.marklynch.weather.livedata.sharedpreferences.SharedPreferencesLiveData
import com.marklynch.weather.sharedpreferences.SHARED_PREFERENCES_CURRENT_LOCATION_ID
import com.marklynch.weather.sharedpreferences.SHARED_PREFERENCES_USE_24_HR_CLOCK
import com.marklynch.weather.sharedpreferences.SHARED_PREFERENCES_USE_CELSIUS
import com.marklynch.weather.sharedpreferences.SHARED_PREFERENCES_USE_KM
import org.koin.standalone.KoinComponent
import org.koin.standalone.get

open class BooleanSharedPreferencesLiveData(
    sharedPreferencesKey: String
) : SharedPreferencesLiveData<Boolean>(sharedPreferencesKey), KoinComponent {

    override fun setLiveDataValue(sharedPreferencesKey: String) {
        postValue(sharedPreferences.getBoolean(sharedPreferencesKey, false))
    }

    fun setSharedPreference(value: Boolean) {
        get<SharedPreferences.Editor>().putBoolean(sharedPreferencesKey, value).apply()
    }
}

class Use24hrClockSharedPreferenceLiveData : BooleanSharedPreferencesLiveData(
    SHARED_PREFERENCES_USE_24_HR_CLOCK
)

class UseKmSharedPreferenceLiveData : BooleanSharedPreferencesLiveData(
    SHARED_PREFERENCES_USE_KM
)

class UseCelsiusSharedPreferenceLiveData : BooleanSharedPreferencesLiveData(
    SHARED_PREFERENCES_USE_CELSIUS
)


