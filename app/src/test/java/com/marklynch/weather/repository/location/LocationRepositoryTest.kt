package com.marklynch.weather.repository.location

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.android.gms.location.LocationResult
import com.marklynch.weather.di.*
import com.marklynch.weather.utils.AppPermissionState
import com.marklynch.weather.utils.observeXTimes
import com.marklynch.weather.utils.setBuildVersionSdkInt
import junit.framework.Assert.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.standalone.StandAloneContext.loadKoinModules
import org.koin.standalone.StandAloneContext.stopKoin
import org.koin.test.KoinTest


class LocationRepositoryTest : KoinTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun setup() {

        val moduleList =
            appModules +
                    activityModules +
                    mockModuleApplication +
                    mockModuleFusedLocationProviderClient +
                    mockModulePermissionsChecker +
                    mockModuleLocationManager

        loadKoinModules(moduleList)
    }

    @After
    fun after() {
        stopKoin()
    }

    @Test
    fun `Test Location Permission Denied`() {

        mockAppPermissionState = AppPermissionState.Denied

        val locationLiveData =
            LocationRepository()

        var observations = 0
        locationLiveData.observeXTimes(1) {
            observations++
            assertEquals(AppPermissionState.Denied, it.locationPermission)
        }

        assertEquals(1, observations)
    }

    @Test
    fun `Test Location Permission Granted`() {

        mockAppPermissionState = AppPermissionState.Granted

        val locationLiveData =
            LocationRepository()

        var observations = 0
        locationLiveData.observeXTimes(1) {
            observations++
            assertEquals(AppPermissionState.Granted, it.locationPermission)
        }

        assertEquals(1, observations)
    }

    @Test
    fun `Test Location GPS Off`() {

        setBuildVersionSdkInt(23)

        mockLocationProviderIsEnabled = false

        val locationLiveData =
            LocationRepository()

        var observations = 0
        locationLiveData.observeXTimes(1) {
            observations++
            assertEquals(GpsState.Disabled, it.gpsState)
        }
    }

    @Test
    fun `Test Location GPS On`() {

        setBuildVersionSdkInt(23)

        mockLocationProviderIsEnabled = true

        val locationLiveData =
            LocationRepository()

        var observations = 0
        locationLiveData.observeXTimes(1) {
            observations++
            assertEquals(GpsState.Enabled, it.gpsState)
        }
    }


    @Test
    fun `Test Location Received`() {

        setBuildVersionSdkInt(23)

        mockAppPermissionState = AppPermissionState.Granted
        mockLocationProviderIsEnabled = true

        val locationLiveData =
            LocationRepository()

        var observations = 0
        locationLiveData.observeXTimes(1) {
            observations++
            assertEquals(AppPermissionState.Granted, it.locationPermission)
        }

        assertEquals(1, observations)

        val locationResultPassed = LocationResult.create(listOf())

        locationLiveData.locationCallback.onLocationResult(locationResultPassed)

        locationLiveData.observeXTimes(1) {
            observations++
            assertEquals(it.lat, locationResultPassed.lastLocation.latitude)
            assertEquals(it.lon, locationResultPassed.lastLocation.longitude)
        }

        assertEquals(2, observations)
    }
}