package com.marklynch.weather.livedata.apppermissions

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

enum class AppPermissionState { Granted, Denied }

const val locationPermission = Manifest.permission.ACCESS_FINE_LOCATION

class AppPermissionLiveData(
    private val context: Context,
    private val permissionToCheck: String
) : LiveData<AppPermissionState>() {

    override fun observe(owner: LifecycleOwner, observer: Observer<in AppPermissionState>) {
        super.observe(owner, observer)
        checkPermission()
    }

    override fun onActive() {
        checkPermission()
    }

    private fun checkPermission() {
        if (isPermissionGranted(context, permissionToCheck))
            postValue(AppPermissionState.Granted)
        else
            postValue(AppPermissionState.Denied)
    }

    private fun isPermissionGranted(context: Context, permissionToCheck: String): Boolean = ActivityCompat.checkSelfPermission(
        context,
        permissionToCheck
    ) == PackageManager.PERMISSION_GRANTED
}