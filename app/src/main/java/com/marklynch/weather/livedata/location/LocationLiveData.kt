package com.marklynch.weather.livedata.location

import android.content.Context
import android.os.Looper
import android.text.format.DateUtils
import androidx.lifecycle.LiveData
import com.google.android.gms.location.*

class LocationLiveData(private val context: Context) : LiveData<LocationResult>() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var locationRequest: LocationRequest

    override fun onActive() {
        super.onActive()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        locationRequest = LocationRequest.create().apply {
            interval = 5 * DateUtils.SECOND_IN_MILLIS
            priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        }

        registerForLocationTracking()
    }

    override fun onInactive() {
        super.onInactive()
        unregisterFromLocationTracking()
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            postValue(locationResult)
        }
    }

    private fun registerForLocationTracking() {
        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest, locationCallback,
                Looper.myLooper()
            )
        } catch (unlikely: SecurityException) {
            error("Error when registerLocationUpdates()")
        }
    }

    private fun unregisterFromLocationTracking() {
        try {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        } catch (unlikely: SecurityException) {
            error("Error when unregisterLocationUpdated()")
        }
    }
}