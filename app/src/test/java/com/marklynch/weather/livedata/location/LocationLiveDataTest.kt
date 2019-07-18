//package com.marklynch.weather.livedata.location
//
//import android.os.Looper
//import android.util.Log
//import androidx.arch.core.executor.testing.InstantTaskExecutorRule
//import androidx.test.InstrumentationRegistry
//import com.google.android.gms.location.LocationResult
//import com.marklynch.weather.livedata1.observeXTimes
//import com.marklynch.weather.view.MainActivity
//import org.junit.Assert.assertNull
//import org.junit.Before
//import org.junit.Rule
//import org.junit.Test
//import org.robolectric.Robolectric
//
//class LocationLiveDataTest {
//
//    @get:Rule
//    val rule = InstantTaskExecutorRule()
//
//    @Before
//    fun setUp() {
//        Log.i("UNITTEST", "Robolectric setting up MainActivity")
//        Looper.prepare()
//        val mainActivity = Robolectric.buildActivity(MainActivity::class.java).create()
//            .start().resume().get()
//    }
//
//    @Test
//    fun testOnActive() {
//        Log.i("UNITTEST", "LocationLiveDataTest.testOnActive")
//        Looper.prepare()
//        val locationLiveData = LocationLiveData(ApplicationProvider.getApplicationContext<MainApplication>())
//        assertNull(locationLiveData.value)
//        validateSingleObservation(locationLiveData)
//
//        // Define a LocationClient object
////        val mLocationClient: LocationClient
////
////        // Connect to Location Services
////        mLocationClient.connect()
//
//        // When the location client is connected, set mock mode
////        mLocationClient.setMockMode(true);
//    }
//
//    private fun validateSingleObservation(locationLiveData: LocationLiveData) {
//
//        Log.i("UNITTEST", "LocationLiveDataTest.validateSingleObservation")
//
//        var observeCount = 0
//        locationLiveData.observeXTimes(1) {
//            checkLocationCorrect(it)
//            observeCount++
//        }
//
//        Thread.sleep(10_000)
//
////        assertEquals(1, observeCount)
////        assertFalse(gpsStatusLiveData.hasActiveObservers())
//    }
//
//    private fun checkLocationCorrect(locationResult: LocationResult) {
//        Log.i("UNITTEST", "LocationLiveDataTest.checkLocationCorrect")
//        Log.i("UNITTEST", "locationResult = $locationResult")
//    }
//}
