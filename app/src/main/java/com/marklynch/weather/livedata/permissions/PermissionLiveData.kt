package com.marklynch.weather.livedata.permissions

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData


enum class PermissionState { Granted, Denied }

val locationPermission = Manifest.permission.ACCESS_FINE_LOCATION

class PermissionLiveData(
    private val context: Context,
    private val permissionToCheck: String
) : LiveData<PermissionState>() {


    override fun onActive() {
        checkPermission()
    }

    private fun checkPermission() {
        val isPermissionGranted = ActivityCompat.checkSelfPermission(
            context,
            permissionToCheck
        ) == PackageManager.PERMISSION_GRANTED

        if (isPermissionGranted)
            postValue(PermissionState.Granted)
        else
            postValue(PermissionState.Denied)
    }
}
