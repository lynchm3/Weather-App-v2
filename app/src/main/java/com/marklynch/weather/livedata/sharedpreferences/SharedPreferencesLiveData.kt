package com.marklynch.weather.livedata.sharedpreferences

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import org.koin.standalone.KoinComponent
import org.koin.standalone.get

abstract class SharedPreferencesLiveData<T>(
    val sharedPreferencesKey: String
) : LiveData<T>(), KoinComponent {

    val sharedPreferences: SharedPreferences = get()

    private val onSharedPreferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == sharedPreferencesKey) {
                setLiveDataValue(sharedPreferencesKey)
            }
        }

    override fun onActive() {
        super.onActive()
        setLiveDataValue(sharedPreferencesKey)
        sharedPreferences.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener)
    }

    override fun onInactive() {
        super.onInactive()
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(
            onSharedPreferenceChangeListener
        )
    }

    abstract fun setLiveDataValue(sharedPreferencesKey: String)
}