package com.marklynch.weather.repository.location

import com.marklynch.weather.utils.AppPermissionState

enum class GpsState { Enabled, Disabled }

data class LocationInformation(
    val locationPermission: AppPermissionState,
    val gpsState: GpsState,
    val lat: Double?,
    val lon: Double?
)