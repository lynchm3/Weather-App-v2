package com.marklynch.weather.livedata.location

import com.google.android.gms.location.LocationResult

enum class GpsState { Enabled, Disabled }

data class LocationInformation(val locationPermission: LocationLiveData.AppPermissionState, val gpsState: GpsState, val locationResult: LocationResult? = null)
{


}