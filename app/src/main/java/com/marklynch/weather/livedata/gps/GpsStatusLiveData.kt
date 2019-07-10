package com.marklynch.weather.livedata.gps

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import android.provider.Settings.Secure.*
import androidx.lifecycle.LiveData


enum class GpsState { Enabled, Disabled }

class GpsStatusLiveData(private val context: Context) : LiveData<GpsState>() {

    private val gpsSwitchStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) = checkGpsStatus()
    }

    override fun onActive() {
        checkGpsStatus()
        registerReceiver()
    }

    override fun onInactive() = unregisterReceiver()

    private fun checkGpsStatus() {
        if (isGpsEnabled()) {
            postValue(GpsState.Enabled)
        } else {
            postValue(GpsState.Disabled)
        }
    }

    private fun isGpsEnabled() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    } else {
        try {
            getInt(context.contentResolver, LOCATION_MODE) != LOCATION_MODE_OFF
        } catch (e: Settings.SettingNotFoundException) {
            false
        }
    }

    private fun registerReceiver() = context.registerReceiver(
        gpsSwitchStateReceiver,
        IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
    )

    private fun unregisterReceiver() = context.unregisterReceiver(gpsSwitchStateReceiver)
}
