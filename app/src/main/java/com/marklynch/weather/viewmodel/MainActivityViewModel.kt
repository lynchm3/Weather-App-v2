package com.marklynch.weather.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.marklynch.weather.livedata.gps.GpsStatusLiveData
import com.marklynch.weather.livedata.apppermissions.AppPermissionLiveData
import com.marklynch.weather.livedata.apppermissions.locationPermission
import com.marklynch.weather.livedata.util.CurrentTimeLiveData
import com.marklynch.weather.webresource.RawWebResourceLiveData

class MainActivityViewModel(application: Application) : BaseActivityViewModel(application) {
    val liveDataFab = MutableLiveData<String>()

    //Time
    val currentTimeLiveData = CurrentTimeLiveData()

    //Raw web resource
    val rawWebResourceLiveData = RawWebResourceLiveData()

    //Location permission
    val locationAppPermissionLiveData = AppPermissionLiveData(application,locationPermission)

    //GPS status
    val gpsStatusLiveData = GpsStatusLiveData(application)

    fun fabClicked() {
//        this.activty.showSnackBar(view, "Setting main text")
        liveDataFab.value = "" + System.currentTimeMillis()
    }




    //LOCATION STUFF
//    private val locationServiceListener = LocationServiceListener(application, Intent(application,
//        LocationService::class.java)
//    )
//
//    private val notificationsUtil = NotificationsUtil(application,
//        application.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//    )
//
//    val gpsStatusLiveData = GpsStatusLiveData(application)
//
//    val locationPermissionStatusLiveData = AppPermissionLiveData(
//        application,
//        Manifest.permission.ACCESS_FINE_LOCATION
//    )
//
//    fun startLocationTracking() = locationServiceListener.subscribeToLocationUpdates()
//
//    fun stopLocationTracking() {
//        locationServiceListener.unsubscribeFromLocationUpdates()
//        notificationsUtil.cancelAlertNotification()
//    }


}