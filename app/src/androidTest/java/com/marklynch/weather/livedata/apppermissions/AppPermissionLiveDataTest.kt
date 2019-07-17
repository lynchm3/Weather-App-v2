package com.marklynch.weather.livedata.apppermissions

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.core.app.ActivityCompat
import androidx.test.InstrumentationRegistry
import androidx.test.core.app.ApplicationProvider
import com.marklynch.weather.MainApplication
import com.marklynch.weather.livedata.observeInfinite
import com.marklynch.weather.livedata.observeXTimes
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test


class AppPermissionLiveDataTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Test
    fun testOnActive() {
        val appPermissionLiveData =
            AppPermissionLiveData(ApplicationProvider.getApplicationContext<MainApplication>(), locationPermission)
        assertNull(appPermissionLiveData.value)
        validateSingleObservation(appPermissionLiveData)
    }

    @Test
    fun testNewObserveTriggersRecheck() {
        val appPermissionLiveData =
            AppPermissionLiveData(ApplicationProvider.getApplicationContext<MainApplication>(), locationPermission)
        assertNull(appPermissionLiveData.value)

        var observeCountForInfiniteObserver = 0
        var observeCountForOnceOffObserver = 0

        appPermissionLiveData.observeInfinite {
            checkAppPermissionStateCorrect(it)
            observeCountForInfiniteObserver++
        }

        assertEquals(2, observeCountForInfiniteObserver)

        appPermissionLiveData.observeXTimes(1) {
            checkAppPermissionStateCorrect(it)
            observeCountForOnceOffObserver++
        }

        assertEquals(1, observeCountForOnceOffObserver)
        assertEquals(3, observeCountForInfiniteObserver)
    }


    private fun validateSingleObservation(appPermissionLiveData: AppPermissionLiveData) {

        var observeCount = 0
        appPermissionLiveData.observeXTimes(1) {
            checkAppPermissionStateCorrect(it)
            observeCount++
        }
        assertEquals(1, observeCount)
        assertFalse("Check live data has no more observers", appPermissionLiveData.hasActiveObservers())
    }

    private fun checkAppPermissionStateCorrect(appPermissionState: AppPermissionState)
    {
        val expectedAppPermissionState = if(ActivityCompat.checkSelfPermission(
                ApplicationProvider.getApplicationContext<MainApplication>(),
                locationPermission) == 0) AppPermissionState.Granted else AppPermissionState.Denied

        assertEquals(expectedAppPermissionState, appPermissionState)
    }
}
