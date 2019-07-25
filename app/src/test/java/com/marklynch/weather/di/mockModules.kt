package com.marklynch.weather.di

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.LocationManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.marklynch.weather.livedata.sharedpreferences.BooleanSharedPreferencesLiveData
import com.marklynch.weather.utils.AppPermissionState
import com.marklynch.weather.utils.PermissionsChecker
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.mock
import okhttp3.HttpUrl
import okhttp3.mockwebserver.MockWebServer
import org.koin.dsl.module.module
import org.mockito.ArgumentMatchers

val mockModuleApplication = module(override = true) {
    single { mock<Application> {} }
    single {
        mock<Context> {
            on { checkSelfPermission(any()) } doAnswer { PackageManager.PERMISSION_DENIED }
        }
        mock<Context> {
            on { registerReceiver(any(), any()) } doAnswer {
                val broadcastReceiver = it.arguments[0] as BroadcastReceiver
                broadcastReceiver.onReceive(get(), Intent())
                null
            }
        }
    }
}

var mockLocationProviderIsEnabled = true
val mockModuleLocationManager = module(override = true) {
    single {
        mock<LocationManager> {
            on { isProviderEnabled(any()) } doAnswer {
                mockLocationProviderIsEnabled
            }
        }
    }
}

var locationCallbackRef: LocationCallback = object : LocationCallback() {
    override fun onLocationResult(newLocationResult: LocationResult) {
        println("locationCallbackRef wasn't set :(")
    }
}

val mockModuleFusedLocationProviderClient = module(override = true) {
    factory {
        mock<FusedLocationProviderClient> {
            on { requestLocationUpdates(any(), any(), any()) } doAnswer {
                locationCallbackRef = it.arguments[1] as LocationCallback
                null
            }
            on { requestLocationUpdates(any(), any()) } doAnswer {
                locationCallbackRef = it.arguments[1] as LocationCallback
                null
            }
        }
    }
}


var mockWebServer: MockWebServer? = null
val mockModuleHttpUrl = module(override = true) {
    factory<HttpUrl> { (baseUrl: String) ->
        mockWebServer?.url("") ?: throw IllegalArgumentException("Illegal URL: \"\"")
    }
}

var mockAppPermissionState = AppPermissionState.Denied
val mockModulePermissionsChecker = module(override = true) {
    single {
        mock<PermissionsChecker> {
            on { getPermissionState(any(), any()) } doAnswer { mockAppPermissionState }
        }
    }
}

var mockSharedPrefString = ""
var mockSharedPrefLong = 0L
var mockSharedPrefBoolean = false
val mockModuleSharedPreferences = module(override = true) {
    single {
        mock<SharedPreferences> {
            on { getString(any(), anyOrNull()) } doAnswer { mockSharedPrefString }
            on { getLong(any(), any()) } doAnswer { mockSharedPrefLong }
            on { getBoolean(any(), any()) } doAnswer { mockSharedPrefBoolean }
        }
    }
}

val mockModuleSharedPreferencesEditor = module(override = true) {
    single {
        mock<SharedPreferences.Editor> {
            on { putString(any(), any()) } doAnswer {
                mockSharedPrefString = it.arguments[1] as String
                this.mock
            }
            on { putLong(any(), any()) } doAnswer {
                mockSharedPrefLong = it.arguments[1] as Long
                this.mock
            }
            on { putBoolean(any(), any()) } doAnswer {
                mockSharedPrefBoolean = it.arguments[1] as Boolean
                this.mock
            }
            on { apply() } doAnswer {
                null
            }
        }
    }
}

var mockSharedPrefLiveDataBoolean = false
val mockModuleBooleanSharedPreferencesLiveData = module(override = true) {
    single {
        mock<BooleanSharedPreferencesLiveData> {
            on { value } doAnswer {
                mockSharedPrefLiveDataBoolean
            }
            on { setSharedPreference(ArgumentMatchers.anyBoolean()) } doAnswer {
                mockSharedPrefLiveDataBoolean = it.arguments[0] as Boolean
                null
            }
        }
    }
}