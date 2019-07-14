package com.marklynch.weather.livedata.location

import com.google.android.gms.location.LocationResult
import com.marklynch.weather.livedata.apppermissions.AppPermissionState

enum class GpsState { Enabled, Disabled }

data class LocationInformation(val locationPermission: AppPermissionState, val gpsState: GpsState, val locationResult: LocationResult? = null)
{


}