package com.marklynch.weather.livedata.apppermissions

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.InstrumentationRegistry
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
            AppPermissionLiveData(InstrumentationRegistry.getTargetContext(), locationPermission)
        assertNull(appPermissionLiveData.value)
        validateSingleObservation(appPermissionLiveData)
    }

    @Test
    fun testNewObserveTriggersRecheck() {
        val appPermissionLiveData =
            AppPermissionLiveData(InstrumentationRegistry.getTargetContext(), locationPermission)
        assertNull(appPermissionLiveData.value)

        var observeCountForInfiniteObserver = 0
        var observeCountForOnceOffObserver = 0

        appPermissionLiveData.observeInfinite {
            assertNotNull(it)
            observeCountForInfiniteObserver++
        }

        assertEquals(2, observeCountForInfiniteObserver)

        appPermissionLiveData.observeXTimes(1) {
            assertNotNull(it)
            observeCountForOnceOffObserver++
        }

        assertEquals(1, observeCountForOnceOffObserver)
        assertEquals(3, observeCountForInfiniteObserver)
    }


    private fun validateSingleObservation(appPermissionLiveData: AppPermissionLiveData) {

        var observeCount = 0
        appPermissionLiveData.observeXTimes(1) {
            assertNotNull(it)
            observeCount++
        }
        assertEquals(1, observeCount)
        assertFalse("Check live data has no more observers", appPermissionLiveData.hasActiveObservers())
    }
}
