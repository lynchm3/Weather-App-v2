package com.marklynch.weather.utils

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat


enum class AppPermissionState { Granted, Denied }
class PermissionsChecker {
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