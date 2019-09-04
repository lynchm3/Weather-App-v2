package com.marklynch.weather.dependencyinjection

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.marklynch.weather.data.WeatherDatabase
import com.marklynch.weather.data.manuallocation.ManualLocation
import com.marklynch.weather.data.manuallocation.ManualLocationDAO
import com.marklynch.weather.livedata.location.LocationInformation
import com.marklynch.weather.livedata.location.LocationLiveData
import com.marklynch.weather.livedata.sharedpreferences.booleansharedpreference.BooleanSharedPreferencesLiveData
import com.marklynch.weather.livedata.sharedpreferences.booleansharedpreference.Use24hrClockSharedPreferenceLiveData
import com.marklynch.weather.livedata.sharedpreferences.booleansharedpreference.UseCelsiusSharedPreferenceLiveData
import com.marklynch.weather.livedata.sharedpreferences.booleansharedpreference.UseKmSharedPreferenceLiveData
import com.marklynch.weather.livedata.sharedpreferences.longsharedpreference.CurrentLocationIdSharedPreferenceLiveData
import com.marklynch.weather.utils.AppPermissionState
import com.marklynch.weather.utils.PermissionsChecker
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.mock
import okhttp3.HttpUrl
import okhttp3.mockwebserver.MockWebServer
import org.koin.dsl.module.module
import org.koin.experimental.builder.single

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
            on { setSharedPreference(any()) } doAnswer {
                mockSharedPrefLiveDataBoolean = it.arguments[0] as Boolean
                null
            }
        }
    }
}

var useCelcius = false
val mockModuleUseCelsiusSharedPreferenceLiveData = module(override = true) {
    single {
        mock<UseCelsiusSharedPreferenceLiveData> {
            on { value } doAnswer {
                useCelcius
            }
            on { setSharedPreference(any()) } doAnswer {
                useCelcius = it.arguments[0] as Boolean
                null
            }
        }
    }
}

var useKm = false
val mockModuleUseKmSharedPreferenceLiveData = module(override = true) {
    single {
        mock<UseKmSharedPreferenceLiveData> {
            on { value } doAnswer {
                useKm
            }
            on { setSharedPreference(any()) } doAnswer {
                useKm = it.arguments[0] as Boolean
                null
            }
        }
    }
}

var use24hrClock = false
val mockModuleUse24hrClockSharedPreferenceLiveData = module(override = true) {
    single {
        mock<Use24hrClockSharedPreferenceLiveData> {
            on { value } doAnswer {
                use24hrClock
            }
            on { setSharedPreference(any()) } doAnswer {
                use24hrClock = it.arguments[0] as Boolean
                null
            }
        }
    }
}


lateinit var mockLocationLiveDataValue: LocationInformation
private val mockLocationLiveData = module(override = true) {

    single {
        mock<LocationLiveData> {
            on { value } doAnswer {
                mockLocationLiveDataValue
            }
        }
    }
}
//val mockLocationLiveData:LocationLiveData = single()

var currentLocationID = 0L
val mockModuleCurrentLocationIdSharedPreferenceLiveData = module(override = true) {
    single {
        mock<CurrentLocationIdSharedPreferenceLiveData> {
            on { value } doAnswer {
                currentLocationID
            }
            on { setSharedPreference(any()) } doAnswer {
                currentLocationID = it.arguments[0] as Long
                null
            }
        }
    }
}

//val mockManualLocationDAO= module(override = true) {
val mockManualLocationDAO = mock<ManualLocationDAO> {
    on { getManualLocationLiveData() } doAnswer {
        mockManualLocationLiveData
    }
}

lateinit var mockManualLocationLiveDataValue: List<ManualLocation>
val mockManualLocationLiveData = mock<LiveData<List<ManualLocation>>> {
    on { value } doAnswer {
        mockManualLocationLiveDataValue
    }
//    on { setSharedPreference(ArgumentMatchers.anyBoolean()) } doAnswer {
//        mockSharedPrefLiveDataBoolean = it.arguments[0] as Boolean
//        null
//    }
}
