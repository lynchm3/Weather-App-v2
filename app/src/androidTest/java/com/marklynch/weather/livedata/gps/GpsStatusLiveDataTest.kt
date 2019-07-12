package com.marklynch.weather.livedata.gps

import android.content.Context
import android.location.LocationManager
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.InstrumentationRegistry
import com.marklynch.weather.livedata.observeXTimes
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test


class GpsStatusLiveDataTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Test
    fun testOnActive() {
        val gpsStatusLiveData = GpsStatusLiveData(InstrumentationRegistry.getTargetContext())
        assertNull(gpsStatusLiveData.value)
        validateSingleObservation(gpsStatusLiveData)
    }

    private fun validateSingleObservation(gpsStatusLiveData: GpsStatusLiveData) {

        var observeCount = 0
        gpsStatusLiveData.observeXTimes(1) {
            checkGpsStateCorrect(it)
            observeCount++
        }

        assertEquals(1, observeCount)
        assertFalse(gpsStatusLiveData.hasActiveObservers())
    }

    private fun checkGpsStateCorrect(gpsState: GpsState) {
        val locationManager =
            InstrumentationRegistry.getTargetContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val expectedGpsState =
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) GpsState.Enabled else GpsState.Disabled
        assertEquals(expectedGpsState, gpsState)
    }
}
