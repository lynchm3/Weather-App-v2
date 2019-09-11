package com.marklynch.weather.livedata.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import android.os.Build
import android.os.Looper
import android.provider.Settings
import androidx.lifecycle.LiveData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.marklynch.weather.utils.AppPermissionState
import com.marklynch.weather.utils.PermissionsChecker
import org.koin.standalone.KoinComponent
import org.koin.standalone.get
import timber.log.Timber


open class LocationLiveData : LiveData<LocationInformation>(), KoinComponent {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var locationResult: LocationResult? = null

    private val locationPermission = Manifest.permission.ACCESS_FINE_LOCATION

    private val locationPermissionState
        get() = get<PermissionsChecker>().getPermissionState(get(), locationPermission)

    private val gpsState
        get() = getGpsState(get())

    private val gpsSwitchStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            postValue(
                LocationInformation(
                    locationPermissionState,
                    gpsState,
                    locationResult?.locations?.get(0)?.latitude,
                    locationResult?.locations?.get(0)?.longitude
                )
            )
            if (locationPermissionState == AppPermissionState.Granted && gpsState == GpsState.Enabled)
                fetchLocation()
        }
    }

    override fun onActive() {
        super.onActive()

        fusedLocationClient = get()

        registerGpsStateReceiver()

        postValue(
            LocationInformation(
                locationPermissionState,
                gpsState,
                locationResult?.locations?.get(0)?.latitude,
                locationResult?.locations?.get(0)?.longitude
            )
        )

        if (locationPermissionState == AppPermissionState.Granted && gpsState == GpsState.Enabled)
            fetchLocation()
    }

    override fun onInactive() {
        super.onInactive()
        unregisterGpsStateReceiver()
        unregisterFromLocationTracking()
    }

    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(newLocationResult: LocationResult) {

            val newLocation = newLocationResult.locations[0]
            if (newLocation != null) {
                postValue(
                    LocationInformation(
                        locationPermissionState, gpsState, newLocationResult.locations[0]?.latitude,
                        newLocationResult.locations[0]?.longitude
                    )
                )
            }
        }
    }


    private fun registerGpsStateReceiver() = get<Context>().registerReceiver(
        gpsSwitchStateReceiver,
        IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
    )

    private fun unregisterGpsStateReceiver() =
        get<Context>().unregisterReceiver(gpsSwitchStateReceiver)

    @SuppressLint("MissingPermission")
    fun fetchLocation() {

        try {
            postValue(
                LocationInformation(
                    locationPermissionState,
                    gpsState,
                    fusedLocationClient.lastLocation.result?.latitude,
                    fusedLocationClient.lastLocation.result?.longitude
                )
            )
        } catch (unlikely: IllegalStateException) {
            Timber.e("Error when fusedLocationClient.lastLocation")
        }

        try {


            fusedLocationClient.requestLocationUpdates(
                LocationRequest.create().apply {
                    priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                    numUpdates = 1
                },
                locationCallback,
                Looper.myLooper()
            )
        } catch (unlikely: SecurityException) {
            Timber.e("Error when registerLocationUpdates()")
        }
    }

    private fun unregisterFromLocationTracking() {
        try {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        } catch (unlikely: SecurityException) {
            Timber.e("Error when unregisterLocationUpdated()")
        }
    }

    private fun getGpsState(context: Context): GpsState {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val locationManager: LocationManager = get()
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                return GpsState.Enabled
        } else {
            try {
                @Suppress("DEPRECATION")
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
}