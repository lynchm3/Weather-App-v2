package com.marklynch.weather.livedata.sharedpreferences

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.lifecycle.LiveData

abstract class SharedPreferencesLiveData<T>(
    val context: Context,
    val sharedPreferencesKey: String
) : LiveData<T>() {

    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    private val onSharedPreferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener {
            sharedPreferences, key ->
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
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener)
    }

    abstract fun setLiveDataValue(sharedPreferencesKey: String)
}