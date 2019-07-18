package com.marklynch.weather.livedata.location

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Looper
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import com.google.android.gms.location.*


class LocationLiveData(private val context: Context) : LiveData<LocationInformation>() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var locationResult: LocationResult? = null

    enum class AppPermissionState { Granted, Denied }

    val locationPermission = Manifest.permission.ACCESS_FINE_LOCATION

    private val locationPermissionState
        get() = getPermissionState(context, locationPermission)

    private val gpsState
        get() = getGpsState(context)

    private val gpsSwitchStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            postValue(LocationInformation(locationPermissionState, gpsState, locationResult))
            if (locationPermissionState == AppPermissionState.Granted && gpsState == GpsState.Enabled)
                fetchLocation()
        }
    }

    override fun onActive() {
        super.onActive()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        registerGpsStateReceiver()

        postValue(LocationInformation(locationPermissionState, gpsState, locationResult))

        if (locationPermissionState == AppPermissionState.Granted && gpsState == GpsState.Enabled)
            fetchLocation()
    }

    override fun onInactive() {
        super.onInactive()
        unregisterGpsStateReceiver()
        unregisterFromLocationTracking()
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(newLocationResult: LocationResult) {
            locationResult = newLocationResult
            postValue(LocationInformation(locationPermissionState, gpsState, locationResult))
        }
    }


    private fun registerGpsStateReceiver() = context.registerReceiver(
        gpsSwitchStateReceiver,
        IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
    )

    private fun unregisterGpsStateReceiver() = context.unregisterReceiver(gpsSwitchStateReceiver)

    fun fetchLocation() {
        try {
            fusedLocationClient.requestLocationUpdates(
                LocationRequest.create().apply {
                    priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                    numUpdates = 1
                }, locationCallback,
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

    fun getGpsState(context: Context): GpsState {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                return GpsState.Enabled
        } else {
            try {
                if (Settings.Secure.getInt(
                        context.contentResolver,
                        Settings.Secure.LOCATION_MODE
                    ) != Settings.Secure.LOCATION_MODE_OFF
                )
                    return GpsState.Enabled
            } catch (e: Settings.SettingNotFoundException) {
                return GpsState.Disabled
            }
        }
        return GpsState.Disabled
    }

    fun getPermissionState(context: Context, permissionToCheck: String): AppPermissionState =

        if (ActivityCompat.checkSelfPermission(
                context,
                permissionToCheck
            ) == PackageManager.PERMISSION_GRANTED
        )
            AppPermissionState.Granted
        else
            AppPermissionState.Denied
}