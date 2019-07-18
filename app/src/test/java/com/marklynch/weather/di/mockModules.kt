package com.marklynch.weather.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.marklynch.weather.livedata.sharedpreferences.BooleanSharedPreferencesLiveData
import com.nhaarman.mockitokotlin2.*
import org.koin.dsl.module.module
import org.mockito.ArgumentMatchers
import org.mockito.Mockito

val mockModuleApplication = module(override = true) {
    single { Mockito.mock(Application::class.java) }
    single { Mockito.mock(Context::class.java) }
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